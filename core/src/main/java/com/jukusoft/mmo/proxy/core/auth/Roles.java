package com.jukusoft.mmo.proxy.core.auth;

public enum Roles {

    GAMEMASTER(0),

    SUPPORT(1),

    DEVELOPER(2),

    ADMINISTRATOR(3),

    QA_TEAM(4);

    private final int resultCode;

    Roles (int resultCode) {
        this.resultCode = resultCode;
    }

    public int getValue() {
        return this.resultCode;
    }

    public static int countRoles () {
        return Roles.values().length;
    }

}
