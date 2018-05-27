package com.jukusoft.mmo.proxy.database.login;

import com.jukusoft.mmo.proxy.core.login.LoginService;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;

public class LDAPLogin implements LoginService {

    //https://cweiske.de/tagebuch/ldap-server-travis.htm

    //https://www.hascode.com/2016/07/ldap-testing-with-java-apacheds-vs-embedded-ldap-junit/

    protected String host = "";
    protected int port = 389;

    public LDAPLogin () {
        //
    }

    public void loadConfig (File configFile) throws IOException {
        Ini ini = new Ini(configFile);
        Profile.Section section = ini.get("LDAP");

        this.host = section.get("host");
        this.port = getInt(section, "port");
    }

    protected int getInt (Profile.Section section, String key) {
        return Integer.parseInt(section.get(key));
    }

    @Override
    public int login(String username, String password) {
        return 0;
    }

}
