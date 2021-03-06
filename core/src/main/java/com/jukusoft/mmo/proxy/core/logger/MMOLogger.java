package com.jukusoft.mmo.proxy.core.logger;

import com.jukusoft.mmo.proxy.core.config.Config;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

public class MMOLogger {

    //queue with logs to send
    protected static final Queue<String> logQueue = new ArrayBlockingQueue<>(Config.MAX_LOG_QUEUE_ENTRIES);
    protected static EventBus eventBus = null;
    protected static long timerID = 0;

    protected static int serverID = 0;

    protected static final Level LOG_LEVEL_FATAL = new CustomLevel("WARNING", 1000);

    private MMOLogger () {
        //
    }

    public static void log (Level level, String tag, String msg, JsonObject params) {
        if (logQueue.size() > Config.MAX_LOG_QUEUE_ENTRIES) {
            System.err.println("logger queue is full, drop log message");
            return;
        }

        if (level.intValue() >= 900) {
            //log messages with levels warning or severe
            System.err.println("[" + level.getName().toUpperCase() + "] " + tag + ": " + msg);
        }

        System.out.println("[" + level.getName() + "] " + msg);

        //add log to queue
        JsonObject json = new JsonObject();
        json.put("serverID", serverID);
        json.put("unixtime", System.currentTimeMillis());
        json.put("level", level.getName());
        json.put("tag", tag);
        json.put("message", msg);
        json.put("params", params);

        String jsonStr = json.encode();

        if (logQueue.size() >= 1000) {
            //log queue is full, drop log message
            System.err.println("log queue is full!");
        } else {
            logQueue.add(jsonStr);
        }
    }

    public static void log (Level level, String tag, String msg) {
        log(level, tag, msg, null);
    }

    public static void log (Level level, String msg) {
        log(level, "Main", msg);
    }

    public static void warn (String tag, String msg) {
        log(Level.WARNING, tag, msg);
    }

    public static void warn (String tag, String msg, Throwable e) {
        log(Level.WARNING, tag, msg + ", throwable: " + e.getLocalizedMessage());
    }

    public static void fatal (String tag, String msg, Throwable e) {
        log(Level.SEVERE, tag, msg + ", throwable: " + e.getLocalizedMessage());
    }

    public static void fatal (String tag, String msg) {
        log(Level.SEVERE, tag, msg);
    }

    public static void info (String tag, String msg) {
        log(Level.INFO, tag, msg);
    }

    /**
    * send logs from queue to server
    */
    public static void sendLogs () {
        if (eventBus == null) {
            //eventbus is not initialized, so we cannot send logs yet
            return;
        }

        for (int i = 0; i < Config.MAX_ITERATIONS_PER_LOGS_SENDING; i++) {
            JsonArray array = new JsonArray();

            for (int k = 0; k < Config.MAX_LOGS_PER_PACKET; k++) {
                String logEvent = logQueue.poll();

                if (logEvent == null) {
                    break;
                }

                array.add(logEvent);
            }

            final String jsonStr = array.encode();

            //send json array to log server
            eventBus.send("persistent-logs", jsonStr);

            if (logQueue.isEmpty()) {
                break;
            }
        }
    }

    public static void init (Vertx vertx) {
        //set eventbus
        MMOLogger.eventBus = vertx.eventBus();

        //add timer
        timerID = vertx.setPeriodic(Config.LOG_INTERVAL, id -> sendLogs());
    }

    public static int getServerID () {
        return serverID;
    }

}
