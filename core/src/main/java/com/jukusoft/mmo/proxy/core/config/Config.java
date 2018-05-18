package com.jukusoft.mmo.proxy.core.config;

public class Config {

    /**
    * logging configuration
    */
    public static final int MAX_LOG_QUEUE_ENTRIES = 100;
    public static final int MAX_LOGS_PER_PACKET = 20;
    public static final int MAX_ITERATIONS_PER_LOGS_SENDING = 5;
    public static final int LOG_INTERVAL = 5000;//send logs to log server every 5 seconds

}
