package com.jukusoft.mmo.proxy.core.handler.impl;

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

    public AuthHandler (LoginService loginService, ICharacterService characterService, KeyPair keyPair) {
        this.loginService = loginService;
        this.characterService = characterService;
        this.keyPair = keyPair;
    }

    @Override
    public void handle(Buffer content, byte type, byte extendedType, ClientConnection conn, ConnectionState state) {
        if (extendedType == Config.MSG_EXTENDED_TYPE_LOGIN_REQUEST) {
            MMOLogger.info("AuthHandler", "login request received.");

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

                MMOLogger.info("Login", "try to login user '" + username + "'...");

                int userID = loginService.login(username, password, conn.getIP());

                if (userID != 0) {
                    //login successfully
                    MMOLogger.info("Login", "login successful for user '" + username + "'.");

                    //update state
                    state.setUserID(userID);

                    //send  message back to client
                    Buffer msg = MessageUtils.createLoginResponse(true, userID);
                    conn.sendToClient(msg);
                } else {
                    //wrong credentials
                    MMOLogger.warn("Login", "login failed for user '" + username + "'.");

                    //send  message back to client
                    Buffer msg = MessageUtils.createLoginResponse(false, userID);
                    conn.sendToClient(msg);
                }
            } catch (Exception e) {
                MMOLogger.warn("ClientConnection", "exception while decrypting data", e);
            }

            return;
        } else if (extendedType == Config.MSG_EXTENDED_TYPE_LIST_CHARACTERS_REQUEST) {
            MMOLogger.info("AuthHandler", "character slots request received.");

            //first, check if user is logged in
            if (!state.isLoggedIn()) {
                MMOLogger.warn("AuthHandler", "Cannot send character slots, because user isnt logged in.");
                return;
            }

            MMOLogger.info("AuthHandler", "send character slots response to client.");

            //send response
            Buffer msg = MessageUtils.createCharacterListResponse(this.characterService.listSlotsOfUser(state.getUserID()));
            conn.sendToClient(msg);

        } else {
            MMOLogger.warn("AuthHandler", "Unknown extended type: " + ByteUtils.byteToHex(extendedType));
        }
    }

}
