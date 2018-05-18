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
    protected static final Queue<String> logQueue = new ArrayBlockingQueue<String>(Config.MAX_LOG_QUEUE_ENTRIES);
    protected static EventBus eventBus = null;
    protected static long timerID = 0;

    public static void log (Level level, String tag, String msg, JsonObject params) {
        System.out.println("[" + level.getName() + "] " + msg);

        //TODO: send logs to centralized log server
    }

    public static void log (Level level, String tag, String msg) {
        log(level, msg, null);
    }

    public static void log (Level level, String msg) {
        log(level, "Main", null);
    }

    public static void warn (String tag, String msg) {
        log(Level.WARNING, tag, msg);
    }

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
        timerID = vertx.setPeriodic(Config.LOG_INTERVAL, id -> {
            //send logs to log server
            sendLogs();
        });
    }

}
