package com.jukusoft.mmo.proxy.core.utils;

import com.jukusoft.mmo.proxy.core.character.CharacterSlot;
import com.jukusoft.mmo.proxy.core.config.Config;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageUtilsTest {

    @Test
    public void testConstructor () {
        new MessageUtils();
    }

    @Test
    public void testCreateMessage () {
        Buffer content = MessageUtils.createMsg(Config.MSG_TYPE_GS, Config.MSG_EXTENDED_TYPE_JOIN, 10);

        //check header
        assertEquals(Config.MSG_TYPE_GS, content.getByte(0));
        assertEquals(Config.MSG_EXTENDED_TYPE_JOIN, content.getByte(1));
        assertEquals(Config.MSG_PROTOCOL_VERSION, content.getShort(2));
        assertEquals(10, content.getInt(4));
    }

    @Test
    public void testCreateRTTMessage () {
        Buffer content = MessageUtils.createRTTResponse();

        //check header
        assertEquals(Config.MSG_TYPE_PROXY, content.getByte(0));
        assertEquals(Config.MSG_EXTENDED_TYPE_RTT, content.getByte(1));
        assertEquals(Config.MSG_PROTOCOL_VERSION, content.getShort(2));
        assertEquals(0, content.getInt(4));
    }

    @Test
    public void testCreatePublicKeyResponseMessage () throws Exception {
        //generate public key
        PublicKey publicKey = EncryptionUtils.generateKeyPair().getPublic();

        Buffer content = MessageUtils.createPublicKeyResponse(publicKey);

        //check header
        assertEquals(Config.MSG_TYPE_PROXY, content.getByte(0));
        assertEquals(Config.MSG_EXTENDED_TYPE_PUBLIC_KEY_RESPONSE, content.getByte(1));
        assertEquals(Config.MSG_PROTOCOL_VERSION, content.getShort(2));
        assertEquals(0, content.getInt(4));

        //check content
        int length = content.getInt(Config.MSG_BODY_OFFSET);
        byte[] array = content.getBytes(Config.MSG_BODY_OFFSET + 4, Config.MSG_BODY_OFFSET + 4 + length);

        //convert array to public key
        PublicKey publicKey1 = EncryptionUtils.getPubKeyFromArray(array);
        assertEquals(publicKey, publicKey1);
    }

    @Test
    public void testCreateLoginResponseMessage () throws Exception {
        Buffer content = MessageUtils.createLoginResponse(false, 1);

        //check header
        assertEquals(Config.MSG_TYPE_AUTH, content.getByte(0));
        assertEquals(Config.MSG_EXTENDED_TYPE_LOGIN_RESPONSE, content.getByte(1));
        assertEquals(Config.MSG_PROTOCOL_VERSION, content.getShort(2));
        assertEquals(0, content.getInt(4));

        //check content
        int userID = content.getInt(Config.MSG_BODY_OFFSET);

        //userID has to be 0, because login failed
        assertEquals(0, userID);
    }

    @Test
    public void testCreateLoginResponseMessage1 () throws Exception {
        Buffer content = MessageUtils.createLoginResponse(true, 10);

        //check header
        assertEquals(Config.MSG_TYPE_AUTH, content.getByte(0));
        assertEquals(Config.MSG_EXTENDED_TYPE_LOGIN_RESPONSE, content.getByte(1));
        assertEquals(Config.MSG_PROTOCOL_VERSION, content.getShort(2));
        assertEquals(0, content.getInt(4));

        //check content
        int userID = content.getInt(Config.MSG_BODY_OFFSET);

        //userID has to be 0, because login failed
        assertEquals(10, userID);
    }

    @Test
    public void testCreateCharacterListResponseMessage () throws Exception {
        //empty slots
        Buffer content = MessageUtils.createCharacterListResponse(new ArrayList<>());

        //check header
        assertEquals(Config.MSG_TYPE_AUTH, content.getByte(0));
        assertEquals(Config.MSG_EXTENDED_TYPE_LIST_CHARACTERS_RESPONSE, content.getByte(1));
        assertEquals(Config.MSG_PROTOCOL_VERSION, content.getShort(2));
        assertEquals(0, content.getInt(4));

        //check content
        int length = content.getInt(Config.MSG_BODY_OFFSET);
        String jsonStr = content.getString(Config.MSG_BODY_OFFSET + 4, Config.MSG_BODY_OFFSET + 4 + length);

        JsonObject json = new JsonObject(jsonStr);
        JsonArray array = json.getJsonArray("slots");
        assertEquals(0, array.size());
    }

    @Test
    public void testCreateCharacterListResponseMessage1 () throws Exception {
        List<CharacterSlot> slots = new ArrayList<>();
        slots.add(CharacterSlot.createDummyMaleCharacterSlot());

        //empty slots
        Buffer content = MessageUtils.createCharacterListResponse(slots);

        //check header
        assertEquals(Config.MSG_TYPE_AUTH, content.getByte(0));
        assertEquals(Config.MSG_EXTENDED_TYPE_LIST_CHARACTERS_RESPONSE, content.getByte(1));
        assertEquals(Config.MSG_PROTOCOL_VERSION, content.getShort(2));
        assertEquals(0, content.getInt(4));

        //check content
        int length = content.getInt(Config.MSG_BODY_OFFSET);
        String jsonStr = content.getString(Config.MSG_BODY_OFFSET + 4, Config.MSG_BODY_OFFSET + 4 + length);

        JsonObject json = new JsonObject(jsonStr);
        JsonArray array = json.getJsonArray("slots");
        assertEquals(1, array.size());
    }

    @Test
    public void testCreateCharacterListResponseMessage2 () throws Exception {
        List<CharacterSlot> slots = new ArrayList<>();
        slots.add(CharacterSlot.createDummyMaleCharacterSlot());
        slots.add(CharacterSlot.createDummyFemaleCharacterSlot());

        //empty slots
        Buffer content = MessageUtils.createCharacterListResponse(slots);

        //check header
        assertEquals(Config.MSG_TYPE_AUTH, content.getByte(0));
        assertEquals(Config.MSG_EXTENDED_TYPE_LIST_CHARACTERS_RESPONSE, content.getByte(1));
        assertEquals(Config.MSG_PROTOCOL_VERSION, content.getShort(2));
        assertEquals(0, content.getInt(4));

        //check content
        int length = content.getInt(Config.MSG_BODY_OFFSET);
        String jsonStr = content.getString(Config.MSG_BODY_OFFSET + 4, Config.MSG_BODY_OFFSET + 4 + length);

        JsonObject json = new JsonObject(jsonStr);
        JsonArray array = json.getJsonArray("slots");
        assertEquals(2, array.size());
    }

    @Test
    public void testCreateCharacterResponseMessage () {
        Buffer content = MessageUtils.createCharacterResponse(20);

        //check header
        assertEquals(Config.MSG_TYPE_AUTH, content.getByte(0));
        assertEquals(Config.MSG_EXTENDED_TYPE_CREATE_CHARACTER_RESPONSE, content.getByte(1));
        assertEquals(Config.MSG_PROTOCOL_VERSION, content.getShort(2));
        assertEquals(0, content.getInt(4));

        //result code
        assertEquals(20, content.getInt(Config.MSG_BODY_OFFSET));
    }

    @Test
    public void testSelectCharacterResponseMessage () {
        Buffer content = MessageUtils.createSelectCharacterResponse(false);

        //check header
        assertEquals(Config.MSG_TYPE_AUTH, content.getByte(0));
        assertEquals(Config.MSG_EXTENDED_TYPE_SELECT_CHARACTER_RESPONSE, content.getByte(1));
        assertEquals(Config.MSG_PROTOCOL_VERSION, content.getShort(2));
        assertEquals(0, content.getInt(4));

        //result code
        assertEquals(0, content.getInt(Config.MSG_BODY_OFFSET));
    }

    @Test
    public void testSelectCharacterResponseMessage1 () {
        Buffer content = MessageUtils.createSelectCharacterResponse(true);

        //check header
        assertEquals(Config.MSG_TYPE_AUTH, content.getByte(0));
        assertEquals(Config.MSG_EXTENDED_TYPE_SELECT_CHARACTER_RESPONSE, content.getByte(1));
        assertEquals(Config.MSG_PROTOCOL_VERSION, content.getShort(2));
        assertEquals(0, content.getInt(4));

        //result code
        assertEquals(1, content.getInt(Config.MSG_BODY_OFFSET));
    }

    @Test
    public void testCreateErrorMsg () {
        Buffer content = MessageUtils.createErrorMsg(Config.MSG_EXTENDED_TYPE_INTERNAL_SERVER_ERROR, 200);

        //check header
        assertEquals(Config.MSG_TYPE_ERROR, content.getByte(0));
        assertEquals(Config.MSG_EXTENDED_TYPE_INTERNAL_SERVER_ERROR, content.getByte(1));
        assertEquals(Config.MSG_PROTOCOL_VERSION, content.getShort(2));
        assertEquals(200, content.getInt(4));
    }

}
