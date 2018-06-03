package com.jukusoft.mmo.proxy.core.utils;

import com.jukusoft.mmo.proxy.core.character.CharacterSlot;
import com.jukusoft.mmo.proxy.core.config.Config;
import com.jukusoft.mmo.proxy.core.logger.MMOLogger;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.security.PublicKey;
import java.util.List;

public class MessageUtils {

    protected MessageUtils () {
        //
    }

    public static Buffer createMsg (byte type, byte extendedType, int cid) {
        Buffer content = Buffer.buffer();

        content.setByte(0, type);
        content.setByte(1, extendedType);
        content.setShort(2, Config.MSG_PROTOCOL_VERSION);
        content.setInt(4, cid);

        return content;
    }

    public static Buffer createRTTResponse () {
        Buffer content = Buffer.buffer();

        content.setByte(0, Config.MSG_TYPE_PROXY);
        content.setByte(1, Config.MSG_EXTENDED_TYPE_RTT);
        content.setShort(2, Config.MSG_PROTOCOL_VERSION);
        content.setInt(4, 0);

        return content;
    }

    public static Buffer createPublicKeyResponse (PublicKey publicKey) {
        Buffer content = Buffer.buffer();

        content.setByte(0, Config.MSG_TYPE_PROXY);
        content.setByte(1, Config.MSG_EXTENDED_TYPE_PUBLIC_KEY_RESPONSE);
        content.setShort(2, Config.MSG_PROTOCOL_VERSION);
        content.setInt(4, 0);

        //convert public key to byte array
        byte[] array = EncryptionUtils.convertPublicKeyToByteArray(publicKey);

        //set length of public key
        content.setInt(Config.MSG_BODY_OFFSET, array.length);

        //set array
        content.setBytes(Config.MSG_BODY_OFFSET + 4, array);

        return content;
    }

    public static Buffer createLoginResponse (boolean success, int userID) {
        Buffer content = Buffer.buffer();

        content.setByte(0, Config.MSG_TYPE_AUTH);
        content.setByte(1, Config.MSG_EXTENDED_TYPE_LOGIN_RESPONSE);
        content.setShort(2, Config.MSG_PROTOCOL_VERSION);
        content.setInt(4, 0);

        //set length of public key
        content.setInt(Config.MSG_BODY_OFFSET, (success ? userID : 0));

        return content;
    }

    public static Buffer createCharacterListResponse (List<CharacterSlot> list) {
        Buffer content = Buffer.buffer();

        content.setByte(0, Config.MSG_TYPE_AUTH);
        content.setByte(1, Config.MSG_EXTENDED_TYPE_LIST_CHARACTERS_RESPONSE);
        content.setShort(2, Config.MSG_PROTOCOL_VERSION);
        content.setInt(4, 0);

        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();

        for (CharacterSlot slot : list) {
            JsonObject json1 = slot.toJson();
            array.add(json1);
        }

        json.put("slots", array);

        //convert json object to string
        String jsonStr = json.encode();

        MMOLogger.info("MessageUtils", "createCharacterListResponse: " + jsonStr);

        content.setInt(Config.MSG_BODY_OFFSET, jsonStr.length());
        content.setString(Config.MSG_BODY_OFFSET + 4, jsonStr);

        MMOLogger.info("MessageUtils", "createCharacterListResponse length: " + content.length());

        return content;
    }

    public static Buffer createSelectCharacterResponse (boolean success) {
        Buffer content = Buffer.buffer();

        content.setByte(0, Config.MSG_TYPE_AUTH);
        content.setByte(1, Config.MSG_EXTENDED_TYPE_SELECT_CHARACTER_RESPONSE);
        content.setShort(2, Config.MSG_PROTOCOL_VERSION);
        content.setInt(4, 0);

        content.setInt(Config.MSG_BODY_OFFSET, (success ? 1 : 0));

        return content;
    }

    public static Buffer createCharacterResponse (int resultCode) {
        Buffer content = Buffer.buffer();

        content.setByte(0, Config.MSG_TYPE_AUTH);
        content.setByte(1, Config.MSG_EXTENDED_TYPE_CREATE_CHARACTER_RESPONSE);
        content.setShort(2, Config.MSG_PROTOCOL_VERSION);
        content.setInt(4, 0);

        content.setInt(Config.MSG_BODY_OFFSET, resultCode);

        return content;
    }

    public static Buffer createLoadRegionMessage (int regionID, int instanceID, String regionTitle) {
        Buffer content = Buffer.buffer();

        content.setByte(0, Config.MSG_TYPE_GENERAL_CLIENT_STATE_INFORMATION);
        content.setByte(1, Config.MSG_EXTENDED_TYPE_LOAD_REGION);
        content.setShort(2, Config.MSG_PROTOCOL_VERSION);
        content.setInt(4, 0);

        //set regionID & instanceID
        content.setInt(Config.MSG_BODY_OFFSET, regionID);
        content.setInt(Config.MSG_BODY_OFFSET + 4, instanceID);

        //set region title
        content.setInt(Config.MSG_BODY_OFFSET + 8, regionTitle.getBytes().length);
        content.setBytes(Config.MSG_BODY_OFFSET + 12, regionTitle.getBytes());

        return content;
    }

    public static Buffer createErrorMsg (byte extendedType, int cid) {
        Buffer content = Buffer.buffer();

        content.setByte(0, Config.MSG_TYPE_ERROR);
        content.setByte(1, extendedType);
        content.setShort(2, Config.MSG_PROTOCOL_VERSION);
        content.setInt(4, cid);

        return content;
    }

}
