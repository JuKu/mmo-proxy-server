package com.jukusoft.mmo.proxy.core.service.connection;

import com.jukusoft.mmo.proxy.core.message.MessageReceiver;
import io.vertx.core.buffer.Buffer;

public class Connection {

    protected final ConnectionState state = new ConnectionState();
    protected long connID = 0;
    protected IConnectionManager manager = null;

    public Connection () {
        //
    }

    public void init (IConnectionManager manager) {
        this.manager = manager;
    }

    public void send (Buffer content) {
        //
    }

    public void setReceiver (MessageReceiver<Buffer> receiver) {
        //
    }

    public void close () {
        //
    }

    public ConnectionState getState () {
        //
    }

    public long getConnID () {
        return this.connID;
    }

    public void setConnID (final long connID) {
        this.connID = connID;
    }

    public int countBackendConnections () {
        return 0;
    }

}
