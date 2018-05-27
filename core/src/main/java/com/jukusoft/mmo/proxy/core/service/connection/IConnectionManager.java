package com.jukusoft.mmo.proxy.core.service.connection;

import com.jukusoft.mmo.proxy.core.handler.MessageHandler;
import com.jukusoft.mmo.proxy.core.service.IService;
import io.vertx.core.buffer.Buffer;

import java.security.KeyPair;

public interface IConnectionManager extends IService {

    public void addConnection (String ip, int port, ClientConnection conn, GSConnectionManager gsConnectionManager);

    public void removeConnection (ClientConnection conn);

    public int countOpenFrontendConnections ();

    public int countOpenBackendConnections ();

    public KeyPair getKeyPair ();

    /**
    * add message handler for specific type
    */
    public void addProxyMessageHandler (byte type, MessageHandler<Buffer> handler);

    public MessageHandler<Buffer> getProxyHandler (byte type, byte extendedType, short version);

}
