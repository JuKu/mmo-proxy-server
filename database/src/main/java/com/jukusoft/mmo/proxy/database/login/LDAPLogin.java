package com.jukusoft.mmo.proxy.database.login;

import com.jukusoft.mmo.proxy.core.login.LoginService;

import java.io.File;

public class LDAPLogin implements LoginService {

    //https://cweiske.de/tagebuch/ldap-server-travis.htm

    public LDAPLogin (File configFile) {
        //
    }

    @Override
    public int login(String username, String password) {
        return 0;
    }

}
