package com.jukusoft.mmo.proxy.main;

import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.jukusoft.mmo.proxy.main.vertx.VertxManager;

public class ServerMain {

    public static void main (String[] args) {
        System.out.println("========= Proxy Server ========");

        //create new hazelcast instance
        System.out.println("Create hazelcast instance...");
        HazelcastInstance hazelcastInstance = createHazelcastInstance();

        //create new vert.x instance
        System.out.println("Create vertx.io instance...");
        VertxManager vertxManager = new VertxManager();
        vertxManager.init(hazelcastInstance);
    }

    public static HazelcastInstance createHazelcastInstance () {
        //create an new hazelcast instance
        Config config = new Config();

        //disable hazelcast logging
        config.setProperty("hazelcast.logging.type", "none");

        CacheSimpleConfig cacheConfig = new CacheSimpleConfig();
        config.getCacheConfigs().put("session-cache", cacheConfig);

        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(config);

        return hazelcastInstance;
    }

}
