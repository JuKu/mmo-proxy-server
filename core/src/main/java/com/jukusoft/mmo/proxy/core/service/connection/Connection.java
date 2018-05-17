package com.jukusoft.mmo.proxy.core.service.connection;

import com.jukusoft.mmo.proxy.core.message.MessageReceiver;
import io.vertx.core.buffer.Buffer;

public interface Connection {

    public void send (Buffer content);

    public void setReceiver (MessageReceiver<Buffer> receiver);

    public void close ();

    public ConnectionState getState ();

}
