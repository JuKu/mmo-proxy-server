package com.jukusoft.mmo.proxy.core.config;

import com.jukusoft.mmo.proxy.core.utils.ByteUtils;
import io.vertx.core.eventbus.DeliveryOptions;

public class Config {

    protected Config () {
        //
    }

    /**
    * logging configuration
    */
    public static final int MAX_LOG_QUEUE_ENTRIES = 100;
    public static final int MAX_LOGS_PER_PACKET = 20;
    public static final int MAX_ITERATIONS_PER_LOGS_SENDING = 5;
    public static final int LOG_INTERVAL = 5000;//send logs to log server every 5 seconds

    //tags
    public static final String LOG_TAG_CLIENT_CONNECTION = "client-connection";

    //vertx eventbus delivery options
    public static final DeliveryOptions EVENTBUS_DELIVERY_OPTIONS = new DeliveryOptions();
    public static final int TCP_CLIENT_CONNECTION_TIMEOUT = 1000;
    public static final int TCP_RECONNECT_ATTEMPTS = 10;
    public static final int TCP_RECONNECT_INTERVAL = 500;

    /**
    * network message type
    */
    public static final byte MSG_CLOSE_CONN = 0x0A;

    public static final boolean[] MSG_REDIRECT_TYPES = new boolean[256];
    public static final boolean[] MSG_SPECIAL_PROXY_TYPES = new boolean[256];//types which should be handled from proxy directly
    public static final boolean[] MSG_INTERNAL_TYPES = new boolean[256];

    static {
        //initialize arrays
        for (int i = 0; i < 256; i++) {
            MSG_REDIRECT_TYPES[i] = false;
            MSG_SPECIAL_PROXY_TYPES[i] = false;
            MSG_INTERNAL_TYPES[i] = false;
        }

        //message types which should be redirected directly to game server (if logged in)
        MSG_REDIRECT_TYPES[ByteUtils.byteToUnsignedInt((byte) 0x03)] = true;//movement
        MSG_REDIRECT_TYPES[ByteUtils.byteToUnsignedInt((byte) 0x07)] = true;//admin stuff (manage worlds, users, npc's and so on)
        MSG_REDIRECT_TYPES[ByteUtils.byteToUnsignedInt((byte) 0x08)] = true;//admin stuff (reserve)
        MSG_REDIRECT_TYPES[ByteUtils.byteToUnsignedInt((byte) 0x09)] = true;//game world information (weather, lighing, download tiled map and so on)

        MSG_SPECIAL_PROXY_TYPES[ByteUtils.byteToUnsignedInt((byte) 0x01)] = true;
        MSG_SPECIAL_PROXY_TYPES[ByteUtils.byteToUnsignedInt((byte) 0x02)] = true;

        //set internal types
        MSG_INTERNAL_TYPES[ByteUtils.byteToUnsignedInt((byte) 0x01)] = true;//proxy - gs communication

        //set timeout of 500ms
        EVENTBUS_DELIVERY_OPTIONS.setSendTimeout(500);
    }

    /**
    * protocol information
    */
    public static final int MSG_HEADER_LENGTH = 8;//header length in bytes
    public static final int MSG_HEADER_CID_POS = 4;
    public static final int MSG_BODY_OFFSET = 8;
    public static final short MSG_PROTOCOL_VERSION = 1;

    /**
    * protocol types
    */

    //message types
    public static final byte MSG_TYPE_GS = 0x01;
    public static final byte MSG_TYPE_AUTH = 0x02;

    public static final byte MSG_TYPE_ERROR = 0x0B;

    //type: 0x01 - reserved for proxy - game server communication (client is not allowed to send such messages)
    public static final byte MSG_EXTENDED_TYPE_JOIN = 0x01;//player joins sector
    public static final byte MSG_EXTENDED_TYPE_LEAVE = 0x02;//player leaves sector

    //type 0x0B error messages & hints
    public static final byte MSG_EXTENDED_TYPE_INCOMPATIBLE_CLIENT = 0x01;
    public static final byte MSG_EXTENDED_TYPE_INTERNAL_SERVER_ERROR = 0x02;

}
