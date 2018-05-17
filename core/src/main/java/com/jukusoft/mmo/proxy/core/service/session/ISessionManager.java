package com.jukusoft.mmo.proxy.core.service.session;

import com.jukusoft.mmo.proxy.core.service.IService;

public interface ISessionManager extends IService {

    public Session createSession (String ip, int port);

    public void closeSession (String sessionID);

}
