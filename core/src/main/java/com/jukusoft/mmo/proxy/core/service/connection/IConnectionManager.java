package com.jukusoft.mmo.proxy.core.service.connection;

import com.jukusoft.mmo.proxy.core.service.IService;

public interface IConnectionManager extends IService {

    public void addConnection (String ip, int port, ClientConnection conn);

    public void removeConnection (ClientConnection conn);

    public int countOpenFrontendConnections ();

    public int countOpenBackendConnections ();

}
