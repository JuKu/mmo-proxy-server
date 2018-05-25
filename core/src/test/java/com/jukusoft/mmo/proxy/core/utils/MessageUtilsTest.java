package com.jukusoft.mmo.proxy.core.utils;

import com.jukusoft.mmo.proxy.core.config.Config;
import io.vertx.core.buffer.Buffer;
import org.junit.Test;

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
    public void testCreateErrorMsg () {
        Buffer content = MessageUtils.createErrorMsg(Config.MSG_EXTENDED_TYPE_INTERNAL_SERVER_ERROR, 200);

        //check header
        assertEquals(Config.MSG_TYPE_ERROR, content.getByte(0));
        assertEquals(Config.MSG_EXTENDED_TYPE_INTERNAL_SERVER_ERROR, content.getByte(1));
        assertEquals(Config.MSG_PROTOCOL_VERSION, content.getShort(2));
        assertEquals(200, content.getInt(4));
    }

}
