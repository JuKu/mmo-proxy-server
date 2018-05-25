package com.jukusoft.mmo.proxy.database.session;

import com.jukusoft.mmo.proxy.core.service.session.ISessionManager;
import com.jukusoft.mmo.proxy.core.service.session.Session;

import java.util.UUID;

public class DummySessionManager implements ISessionManager {

    @Override
    public Session createSession(String ip, int port) {
        return new Session(UUID.randomUUID().toString());
    }

    @Override
    public void closeSession(String sessionID) {

    }

}
