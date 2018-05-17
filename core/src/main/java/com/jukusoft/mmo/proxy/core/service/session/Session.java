package com.jukusoft.mmo.proxy.core.service.session;

public class Session {

    protected final String sessionID;

    public Session (String sessionID) {
        this.sessionID = sessionID;
    }

    public String getSessionID () {
        return this.sessionID;
    }

}
