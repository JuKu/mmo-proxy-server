package com.jukusoft.mmo.proxy.core.service.connection;

import com.jukusoft.mmo.proxy.core.service.IService;
import com.jukusoft.mmo.proxy.core.service.session.Session;

public interface IConnectionManager extends IService {

    public Connection addConnection (String ip, int port, Session session);

    public int countOpenFrontendConnections ();

    public int countOpenBackendConnections ();

}
