package com.jukusoft.mmo.proxy.database.login;

import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.ldap.EmbeddedLdapRule;
import org.zapodot.junit.ldap.EmbeddedLdapRuleBuilder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class LDAPLoginTest {

    //https://github.com/zapodot/embedded-ldap-junit

    //https://www.netiq.com/communities/cool-solutions/cool_tools/easily-generate-ldif-file-testing/

    //http://ldapwiki.com/wiki/LDIF%20Generator

    //https://gist.github.com/hasithaa/9597687

    //https://theholyjava.wordpress.com/2010/05/05/mocking-out-ldapjndi-in-unit-tests/

    @Rule
    public EmbeddedLdapRule embeddedLdapRule = EmbeddedLdapRuleBuilder
            .newInstance()
            //.withSchema("schema/core.schema", "schema/cosine.schema", "schema/nis.schema", "schema/inetorgperson.schema")
            .usingDomainDsn("dc=example,dc=com")
            .importingLdifs("example.ldif")
            .build();

    @Test
    public void testConstructor () {
        new LDAPLogin();
    }

    @Test
    public void testLoadConfig () throws IOException {
        LDAPLogin ldapLogin = new LDAPLogin();
        ldapLogin.loadConfig(new File("../config/tests/travis.ldap.cfg"));

        assertEquals("127.0.0.1", ldapLogin.host);
        assertEquals(389, ldapLogin.port);
    }

}
