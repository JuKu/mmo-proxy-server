package com.jukusoft.mmo.proxy.core.service.connection;

public class ConnectionState {

    protected int userID = -1;

    //character id
    protected int cid = -1;

    public ConnectionState () {
        //
    }

    public int getUserID () {
        return this.userID;
    }

    public void setUserID (int userID) {
        this.userID = userID;
    }

    public int getCID () {
        return this.cid;
    }

    public void setCID (int cid) {
        this.cid = cid;
    }

}
