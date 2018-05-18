package com.jukusoft.mmo.proxy.core.service.connection;

import com.jukusoft.mmo.proxy.core.service.IService;
import com.jukusoft.mmo.proxy.core.service.session.Session;

public interface IConnectionManager extends IService {

    public void addConnection (String ip, int port, Connection conn);

    public void removeConnection (Connection conn);

    public int countOpenFrontendConnections ();

    public int countOpenBackendConnections ();

}
