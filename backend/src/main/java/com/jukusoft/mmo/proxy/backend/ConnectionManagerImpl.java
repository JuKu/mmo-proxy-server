package com.jukusoft.mmo.proxy.backend;

import com.jukusoft.mmo.proxy.core.service.connection.Connection;
import com.jukusoft.mmo.proxy.core.service.connection.IConnectionManager;
import com.jukusoft.mmo.proxy.core.service.session.Session;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

public class ConnectionManagerImpl implements IConnectionManager {

    protected Vertx vertx = null;

    //vertx.io eventbus
    protected EventBus eventBus = null;

    /**
    * default constructor
     *
     * @param vertx instance of vertx
    */
    public ConnectionManagerImpl (Vertx vertx) {
        this.vertx = vertx;
        this.eventBus = vertx.eventBus();
    }

    @Override
    public Connection addConnection(String ip, int port, Session session) {
        return null;
    }

    @Override
    public int countOpenFrontendConnections() {
        return 0;
    }

    @Override
    public int countOpenBackendConnections() {
        return 0;
    }

}
