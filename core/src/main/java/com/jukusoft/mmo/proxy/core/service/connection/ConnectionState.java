package com.jukusoft.mmo.proxy.core.service.connection;

import com.jukusoft.mmo.proxy.core.auth.Roles;

import java.util.BitSet;

public class ConnectionState {

    protected volatile int userID = -1;
    protected BitSet roles = new BitSet(Roles.countRoles());

    //character id
    protected int cid = -1;

    public ConnectionState () {
        //reset bitset
        for (int i = 0; i < roles.length(); i++) {
            roles.set(i, false);
        }
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

    public boolean isLoggedIn () {
        return this.userID > 0;
    }

    public boolean hasRole(Roles role) {
        return this.roles.get(role.getValue());
    }

    public void setRole (Roles role) {
        this.roles.set(role.getValue(), true);
    }

    public void unsetRole (Roles role) {
        this.roles.set(role.getValue(), false);
    }

    public boolean isCharacterSelected () {
        return this.cid > 0;
    }

}
