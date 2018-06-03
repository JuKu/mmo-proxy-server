package com.jukusoft.mmo.proxy.core.handler.impl;

import com.jukusoft.mmo.proxy.core.auth.Roles;
import com.jukusoft.mmo.proxy.core.character.CharacterSlot;
import com.jukusoft.mmo.proxy.core.character.ICharacterService;
import com.jukusoft.mmo.proxy.core.config.Config;
import com.jukusoft.mmo.proxy.core.handler.MessageHandler;
import com.jukusoft.mmo.proxy.core.logger.MMOLogger;
import com.jukusoft.mmo.proxy.core.login.LoginService;
import com.jukusoft.mmo.proxy.core.service.connection.ClientConnection;
import com.jukusoft.mmo.proxy.core.service.connection.ConnectionState;
import com.jukusoft.mmo.proxy.core.utils.ByteUtils;
import com.jukusoft.mmo.proxy.core.utils.EncryptionUtils;
import com.jukusoft.mmo.proxy.core.utils.MessageUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

import java.security.KeyPair;
import java.security.PrivateKey;

public class AuthHandler implements MessageHandler<Buffer> {

    protected final LoginService loginService;
    protected final ICharacterService characterService;
    protected final KeyPair keyPair;

    public static final String LOG_TAG = "AuthHandler";
    public static final String LOG_TAG_LOGIN = "Login";

    public AuthHandler (LoginService loginService, ICharacterService characterService, KeyPair keyPair) {
        this.loginService = loginService;
        this.characterService = characterService;
        this.keyPair = keyPair;
    }

    @Override
    public void handle(Buffer content, byte type, byte extendedType, ClientConnection conn, ConnectionState state) {
        if (extendedType == Config.MSG_EXTENDED_TYPE_LOGIN_REQUEST) {
            MMOLogger.info(LOG_TAG, "login request received.");

            //get private key for decryption
            PrivateKey privateKey = keyPair.getPrivate();

            //decrypt data
            int length = content.getInt(Config.MSG_BODY_OFFSET);
            byte[] encrypted = content.getBytes(Config.MSG_BODY_OFFSET + 4, Config.MSG_BODY_OFFSET + 4 + length);
            try {
                String jsonStr = EncryptionUtils.decrypt(privateKey, encrypted);
                JsonObject json = new JsonObject(jsonStr);

                //get username and password
                String username = json.getString("username");
                String password = json.getString("password");

                MMOLogger.info(LOG_TAG_LOGIN, "try to login user '" + username + "'...");

                int userID = loginService.login(username, password, conn.getIP());

                if (userID != 0) {
                    //login successfully
                    MMOLogger.info(LOG_TAG_LOGIN, "login successful for user '" + username + "'.");

                    //update state
                    state.setUserID(userID);

                    //send  message back to client
                    Buffer msg = MessageUtils.createLoginResponse(true, userID);
                    conn.sendToClient(msg);
                } else {
                    //wrong credentials
                    MMOLogger.warn(LOG_TAG_LOGIN, "login failed for user '" + username + "'.");

                    //send  message back to client
                    Buffer msg = MessageUtils.createLoginResponse(false, userID);
                    conn.sendToClient(msg);
                }
            } catch (Exception e) {
                MMOLogger.warn("ClientConnection", "exception while decrypting data", e);
            }
        } else if (extendedType == Config.MSG_EXTENDED_TYPE_LIST_CHARACTERS_REQUEST) {
            MMOLogger.info(LOG_TAG, "character slots request received.");

            //first, check if user is logged in
            if (!state.isLoggedIn()) {
                MMOLogger.warn(LOG_TAG, "Cannot send character slots, because user isnt logged in.");
                return;
            }

            MMOLogger.info(LOG_TAG, "send character slots response to client.");

            //send response
            Buffer msg = MessageUtils.createCharacterListResponse(this.characterService.listSlotsOfUser(state.getUserID()));
            conn.sendToClient(msg);

        } else if (extendedType == Config.MSG_EXTENDED_TYPE_CREATE_CHARACTER_REQUEST) {
            //create character request
            this.handleCreateCharacterRequest(content, conn, state);
        } else if (extendedType == Config.MSG_EXTENDED_TYPE_SELECT_CHARACTER_REQUEST) {
            //select character request
            this.handleSelectCharacterRequest(content, conn, state);
        } else {
            MMOLogger.warn(LOG_TAG, "Unknown extended type: " + ByteUtils.byteToHex(extendedType));
        }
    }

    protected void handleCreateCharacterRequest (Buffer content, ClientConnection conn, ConnectionState state) {
        MMOLogger.info(LOG_TAG, "received create character request");

        //first, check if user is logged in
        if (!state.isLoggedIn()) {
            MMOLogger.warn(LOG_TAG, "Cannot create character, because user isnt logged in.");
            return;
        }

        int length = content.getInt(Config.MSG_BODY_OFFSET);
        String jsonStr = content.getString(Config.MSG_BODY_OFFSET + 4, Config.MSG_BODY_OFFSET + 4 + length);
        JsonObject json = new JsonObject(jsonStr);
        String name = json.getString("name");

        //convert json object to character
        CharacterSlot slot = CharacterSlot.createFromJson(Integer.MAX_VALUE, name, json);

        //try to create character
        this.characterService.createCharacter(slot, state.getUserID(), resultCode -> {
            MMOLogger.info(LOG_TAG, "send create character result code: " + resultCode);

            //send response back to client
            Buffer msg = MessageUtils.createCharacterResponse(resultCode);
            conn.sendToClient(msg);
        });
    }

    protected void handleSelectCharacterRequest (Buffer content, final ClientConnection conn, final ConnectionState state) {
        MMOLogger.info(LOG_TAG, "received select character request");

        //first, check if user is logged in
        if (!state.isLoggedIn()) {
            MMOLogger.warn(LOG_TAG, "Cannot select character, because user isnt logged in.");
            return;
        }

        int cid = content.getInt(Config.MSG_BODY_OFFSET);

        //flag, if it should be checked, if cid belongs to user - game masters and support staff doesnt requires this check, because they are allowed to control every character
        boolean check = !(state.hasRole(Roles.GAMEMASTER) || state.hasRole(Roles.SUPPORT));

        //check, if cid belongs to user or user is admin
        if (check && !this.characterService.checkCIDBelongsToPlayer(cid, state.getUserID())) {
            //character doesnt belongs to player

            //send error message
            Buffer msg = MessageUtils.createSelectCharacterResponse(false);
            conn.sendToClient(msg);

            MMOLogger.warn(LOG_TAG, "cid " + cid + " doesnt belongs to userID " + state.getUserID());

            return;
        }

        //select cid
        state.setCID(cid);

        MMOLogger.info(LOG_TAG, "character " + cid + " selected successfully for userID " + state.getUserID());

        //send success message
        Buffer msg = MessageUtils.createSelectCharacterResponse(true);
        conn.sendToClient(msg);

        //get current regionID & instanceID of character
        characterService.getCurrentRegionOfCharacter(cid, res -> {
            if (res == null) {
                //TODO: send error message to client

                throw new IllegalStateException("cannot get current region of character (cid: " + cid + "), maybe character doesnt exists in database.");
            }

            MMOLogger.info("AuthHandler", "send load region message to client, regionID: " + res.regionID + ", instanceID: " + res.instanceID + " .");

            //send load region message
            Buffer msg1 = MessageUtils.createLoadRegionMessage(res.regionID, res.instanceID, res.title);
            conn.sendToClient(msg1);

            //TODO: send join message so client goes to region loading screen

            //TODO: open gameserver connection
        });
    }

}
