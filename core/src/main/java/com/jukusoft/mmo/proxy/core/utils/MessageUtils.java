package com.jukusoft.mmo.proxy.core.utils;

import com.jukusoft.mmo.proxy.core.config.Config;
import io.vertx.core.buffer.Buffer;

public class MessageUtils {

    public static Buffer createMsg (byte type, byte extendedType, int cid) {
        Buffer content = Buffer.buffer();

        content.setByte(0, type);
        content.setByte(1, extendedType);
        content.setShort(2, Config.MSG_PROTOCOL_VERSION);
        content.setInt(4, cid);

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
