package com.jukusoft.mmo.proxy.database.login;

import org.junit.Test;

import java.io.File;

public class LDAPLoginTest {

    @Test
    public void testConstructor () {
        new LDAPLogin(new File("../config/tests/travis.ldap.cfg"));
    }

}
