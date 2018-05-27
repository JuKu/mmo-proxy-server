package com.jukusoft.mmo.proxy.core.handler;

import com.jukusoft.mmo.proxy.core.service.connection.ClientConnection;
import com.jukusoft.mmo.proxy.core.service.connection.ConnectionState;

public interface MessageHandler<T> {

    public void handle(T content, byte type, byte extendedType, ClientConnection conn, ConnectionState state);

}
