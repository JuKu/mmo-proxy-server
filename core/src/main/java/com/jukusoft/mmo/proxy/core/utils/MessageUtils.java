package com.jukusoft.mmo.proxy.core.utils;

import com.jukusoft.mmo.proxy.core.config.Config;
import io.vertx.core.buffer.Buffer;

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

    public static Buffer createErrorMsg (byte extendedType, int cid) {
        Buffer content = Buffer.buffer();

        content.setByte(0, Config.MSG_TYPE_ERROR);
        content.setByte(1, extendedType);
        content.setShort(2, Config.MSG_PROTOCOL_VERSION);
        content.setInt(4, cid);

        return content;
    }

}
