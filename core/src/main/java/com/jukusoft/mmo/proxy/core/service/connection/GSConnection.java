package com.jukusoft.mmo.proxy.core.service.connection;

import com.jukusoft.mmo.proxy.core.message.MessageReceiver;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;

public interface GSConnection {

    /**
    * send message to game server
    */
    public void send (Buffer content);

    /**
    * set message receiver for messages from game server
    */
    public void setReceiver (MessageReceiver<Buffer> receiver);

    /**
    * close handler
    */
    public void setCloseHandler (Handler<Void> handler);

    /**
    * check, if connection is opened
    */
    public boolean isOpened ();

    /**
    * close connection
    */
    public void close ();

}
