package com.jukusoft.mmo.proxy.main;

import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.jukusoft.mmo.proxy.backend.ConnectionManagerImpl;
import com.jukusoft.mmo.proxy.core.ProxyServer;
import com.jukusoft.mmo.proxy.core.frontend.IFrontend;
import com.jukusoft.mmo.proxy.core.service.connection.IConnectionManager;
import com.jukusoft.mmo.proxy.frontend.TCPFrontend;
import com.jukusoft.mmo.proxy.main.vertx.VertxManager;
import com.jukusoft.mmo.proxy.management.ManagementFrontend;
import io.vertx.core.Vertx;

import java.util.logging.Logger;

public class ServerMain {
    
    protected static final Logger LOGGER = Logger.getLogger("Main");

    public static void main (String[] args) {
        log("========= Proxy Server ========");

        //create new hazelcast instance
        log("Create hazelcast instance...");
        HazelcastInstance hazelcastInstance = createHazelcastInstance();

        //create new vert.x instance
        log("Create vertx.io instance...");
        VertxManager vertxManager = new VertxManager();
        vertxManager.init(hazelcastInstance);

        //get vertx instance
        Vertx vertx = vertxManager.getVertx();

        //create proxy server
        log("Create proxy server instance...");
        ProxyServer server = new ProxyServer();

        //add services
        server.addService(new ConnectionManagerImpl(vertx), IConnectionManager.class);

        //create tcp frontend
        log("Create tcp frontend on port 2222...");
        server.addFrontend(new TCPFrontend(vertx, 2222, 4), TCPFrontend.class);

        //add new management module
        log("Create management module on port 8089...");
        server.addFrontend(createManagementModule(vertx, 8089), ManagementFrontend.class);

        log("Initialization finished!");
        log("");
        log("Available frontends:");
        
        for (IFrontend frontend : server.listFrontends()) {
            log("  - " + frontend.getName() + " (port: " + frontend.getPort() + ")");
        }

        log("");
        log("All frontends are up!");
    }
    
    protected static void log (String msg) {
        System.out.println(msg);
    }

    public static HazelcastInstance createHazelcastInstance () {
        //create an new hazelcast instance
        Config config = new Config();

        //disable hazelcast logging
        config.setProperty("hazelcast.logging.type", "none");

        CacheSimpleConfig cacheConfig = new CacheSimpleConfig();
        config.getCacheConfigs().put("session-cache", cacheConfig);

        return Hazelcast.newHazelcastInstance(config);
    }

    public static ManagementFrontend createManagementModule (Vertx vertx, int port) {
        return new ManagementFrontend(vertx, port);
    }

}
