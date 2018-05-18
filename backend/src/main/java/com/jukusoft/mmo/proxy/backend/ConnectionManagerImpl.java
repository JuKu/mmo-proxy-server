package com.jukusoft.mmo.proxy.backend;

import com.jukusoft.mmo.proxy.core.logger.MMOLogger;
import com.jukusoft.mmo.proxy.core.service.connection.Connection;
import com.jukusoft.mmo.proxy.core.service.connection.IConnectionManager;
import com.jukusoft.mmo.proxy.core.service.session.Session;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;

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

    protected List<Connection> allConnections = new ArrayList<>();

    /**
    * default constructor
     *
     * @param vertx instance of vertx
    */
    public ConnectionManagerImpl (Vertx vertx) {
        this.vertx = vertx;
        this.eventBus = vertx.eventBus();

        this.deliveryOptions = new DeliveryOptions();

        //set timeout of one second
        this.deliveryOptions.setSendTimeout(1000);

        //register codec for api request & response message
        //eventBus.registerDefaultCodec(ApiRequest.class, new ApiRequestCodec());
        //eventBus.registerDefaultCodec(ApiResponse.class, new ApiResponseCodec());

        //interface for codecs: MessageCodec<ApiRequest, ApiRequest>
    }

    @Override
    public void addConnection(String ip, int port, Connection conn) {
        //create new local unique connection id
        final long connID = this.lastID.incrementAndGet();

        //inform cluster that new connection was opened
        this.eventBus.send("new-connection", ip + ":" + port + ":" + connID, this.deliveryOptions, reply -> {
            if (reply.succeeded()) {
                //reply.result().body()
            }
        });

        //log connection
        MMOLogger.log(Level.INFO, "new-connection", "new connection: " + ip + ":" + port);

        //initialize connection
        conn.init(this);

        //add connectionID
        conn.setConnID(connID);

        this.allConnections.add(conn);

        //increment count of connections
        this.connectionCount++;
    }

    @Override
    public void removeConnection(Connection conn) {
        this.allConnections.remove(conn);
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

        for (Connection conn : this.allConnections) {
            count += conn.countBackendConnections();
        }

        return count;
    }

}
