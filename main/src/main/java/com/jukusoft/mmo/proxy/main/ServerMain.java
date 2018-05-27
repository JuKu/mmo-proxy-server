package com.jukusoft.mmo.proxy.main;

import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.jukusoft.mmo.proxy.backend.ConnectionManagerImpl;
import com.jukusoft.mmo.proxy.backend.GSConnectionManagerImpl;
import com.jukusoft.mmo.proxy.core.ProxyServer;
import com.jukusoft.mmo.proxy.core.frontend.IFrontend;
import com.jukusoft.mmo.proxy.core.handler.impl.AuthHandler;
import com.jukusoft.mmo.proxy.core.logger.MMOLogger;
import com.jukusoft.mmo.proxy.core.login.LoginService;
import com.jukusoft.mmo.proxy.core.service.connection.GSConnectionManager;
import com.jukusoft.mmo.proxy.core.service.connection.IConnectionManager;
import com.jukusoft.mmo.proxy.core.service.firewall.IFirewall;
import com.jukusoft.mmo.proxy.core.service.session.ISessionManager;
import com.jukusoft.mmo.proxy.core.utils.EncryptionUtils;
import com.jukusoft.mmo.proxy.core.utils.Utils;
import com.jukusoft.mmo.proxy.database.DatabaseUpgrader;
import com.jukusoft.mmo.proxy.database.config.MySQLConfig;
import com.jukusoft.mmo.proxy.database.firewall.DummyFirewall;
import com.jukusoft.mmo.proxy.database.login.LDAPLogin;
import com.jukusoft.mmo.proxy.database.session.DummySessionManager;
import com.jukusoft.mmo.proxy.frontend.TCPFrontend;
import com.jukusoft.mmo.proxy.main.vertx.VertxManager;
import com.jukusoft.mmo.proxy.management.ManagementFrontend;
import io.vertx.core.Vertx;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class ServerMain {
    
    protected static final Logger LOGGER = Logger.getLogger("Main");

    public static void main (String[] args) {
        log("========= Proxy Server ========");

        Utils.printSection("Hazelcast");

        //create new hazelcast instance
        log("Create hazelcast instance...");
        HazelcastInstance hazelcastInstance = createHazelcastInstance();

        Utils.printSection("Vertx");

        //create new vert.x instance
        log("Create vertx.io instance...");
        VertxManager vertxManager = new VertxManager();
        vertxManager.init(hazelcastInstance);

        //get vertx instance
        Vertx vertx = vertxManager.getVertx();

        //initialize logger
        MMOLogger.init(vertx);

        Utils.printSection("MySQL");

        //load mysql config
        MySQLConfig mySQLConfig = new MySQLConfig();
        try {
            mySQLConfig.load(new File("./config/mysql.cfg"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        //create or upgrade database schema
        DatabaseUpgrader databaseUpgrader = new DatabaseUpgrader(mySQLConfig);
        databaseUpgrader.migrate();
        System.out.println(databaseUpgrader.getInfo());

        Utils.printSection("RSA Encryption");
        log("generate RSA key pair...");

        KeyPair keyPair = null;

        try {
            keyPair = EncryptionUtils.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Utils.printSection("Proxy Server");
        //create proxy server
        log("Create proxy server instance...");
        ProxyServer server = new ProxyServer();

        IConnectionManager connectionManager = new ConnectionManagerImpl(vertx, keyPair);

        LDAPLogin ldapLogin = new LDAPLogin(new File("./config/ldap.cfg"));

        //add services
        server.addService(connectionManager, IConnectionManager.class);
        server.addService(new GSConnectionManagerImpl(vertx), GSConnectionManager.class);
        server.addService(new DummyFirewall(), IFirewall.class);
        server.addService(new DummySessionManager(), ISessionManager.class);
        server.addService(new LDAPLogin(new File("./config/ldap.cfg")), LoginService.class);

        Utils.printSection("Message Handler");
        log("register message handler...");
        connectionManager.addProxyMessageHandler(com.jukusoft.mmo.proxy.core.config.Config.MSG_TYPE_AUTH, new AuthHandler(server.getService(LoginService.class), keyPair));

        Utils.printSection("Frontend");

        //create tcp frontend
        log("Create tcp frontend on port 2222...");
        server.addFrontend(new TCPFrontend(vertx, 2222, 4), TCPFrontend.class);

        //add new management module
        log("Create management module on port 8089...");
        server.addFrontend(createManagementModule(vertx, 8089), ManagementFrontend.class);

        Utils.printSection("Summary");

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
