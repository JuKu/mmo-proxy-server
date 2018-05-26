package com.jukusoft.mmo.proxy.backend;

import com.jukusoft.mmo.proxy.core.logger.MMOLogger;
import com.jukusoft.mmo.proxy.core.service.connection.ClientConnection;
import com.jukusoft.mmo.proxy.core.service.connection.GSConnectionManager;
import com.jukusoft.mmo.proxy.core.service.connection.IConnectionManager;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

public class ConnectionManagerImpl implements IConnectionManager {

    protected Vertx vertx = null;

    //vertx.io eventbus
    protected EventBus eventBus = null;
    protected DeliveryOptions deliveryOptions = null;
    protected volatile int connectionCount = 0;
    protected AtomicLong lastID = new AtomicLong(0);

    protected List<ClientConnection> allClientConnections = new ArrayList<>();
    protected final KeyPair keyPair;

    /**
    * default constructor
     *
     * @param vertx instance of vertx
    */
    public ConnectionManagerImpl (Vertx vertx, KeyPair keyPair) {
        this.vertx = vertx;
        this.eventBus = vertx.eventBus();

        this.keyPair = keyPair;

        this.deliveryOptions = new DeliveryOptions();

        //set timeout of one second
        this.deliveryOptions.setSendTimeout(1000);
    }

    @Override
    public void addConnection(String ip, int port, ClientConnection conn, GSConnectionManager gsConnectionManager) {
        //create new local unique connection id
        final long connID = this.lastID.incrementAndGet();

        //inform cluster that new connection was opened
        this.eventBus.send("new-connection", ip + ":" + port + ":" + connID, this.deliveryOptions);

        //log connection
        MMOLogger.log(Level.INFO, "new-connection", "new connection: " + ip + ":" + port);

        //initialize connection
        conn.init(this, gsConnectionManager);

        //add connectionID
        conn.setConnID(connID);

        this.allClientConnections.add(conn);

        //increment count of connections
        this.connectionCount++;
    }

    @Override
    public void removeConnection(ClientConnection conn) {
        this.allClientConnections.remove(conn);
        this.connectionCount--;

        //inform cluster that new connection was opened
        this.eventBus.send("closed-connection", "connection closed: " + conn.getConnID());
    }

    @Override
    public int countOpenFrontendConnections() {
        return this.connectionCount;
    }

    @Override
    public int countOpenBackendConnections() {
        int count = 0;

        for (ClientConnection conn : this.allClientConnections) {
            count += conn.countBackendConnections();
        }

        return count;
    }

    @Override
    public KeyPair getKeyPair() {
        return this.keyPair;
    }

}
