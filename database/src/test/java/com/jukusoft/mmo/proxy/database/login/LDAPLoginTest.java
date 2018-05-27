package com.jukusoft.mmo.proxy.database.login;

import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.ldap.EmbeddedLdapRule;
import org.zapodot.junit.ldap.EmbeddedLdapRuleBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class LDAPLoginTest {

    //https://github.com/zapodot/embedded-ldap-junit

    //https://www.netiq.com/communities/cool-solutions/cool_tools/easily-generate-ldif-file-testing/

    //http://ldapwiki.com/wiki/LDIF%20Generator

    //https://gist.github.com/hasithaa/9597687

    //https://theholyjava.wordpress.com/2010/05/05/mocking-out-ldapjndi-in-unit-tests/

    public static final String DOMAIN_DSN = "dc=example,dc=com";

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

    @Test
    public void shouldFindAllPersons() throws Exception {
        final LDAPInterface ldapConnection = embeddedLdapRule.ldapConnection();
        final SearchResult searchResult = ldapConnection.search(DOMAIN_DSN, SearchScope.SUB, "(objectClass=person)");
        assertEquals(3, searchResult.getEntryCount());
        List<SearchResultEntry> searchEntries = searchResult.getSearchEntries();
        assertEquals("John Steinbeck", searchEntries.get(0).getAttribute("cn").getValue());
        assertEquals("Micha Kops", searchEntries.get(1).getAttribute("cn").getValue());
        assertEquals("Santa Claus", searchEntries.get(2).getAttribute("cn").getValue());
    }

    @Test
    public void shouldFindExactPerson() throws Exception {
        final LDAPInterface ldapConnection = embeddedLdapRule.ldapConnection();
        final SearchResult searchResult = ldapConnection.search("cn=Santa Claus,ou=Users,dc=example,dc=com",
                SearchScope.SUB, "(objectClass=person)");
        assertEquals(1, searchResult.getEntryCount());
        assertEquals("Santa Claus", searchResult.getSearchEntries().get(0).getAttribute("cn").getValue());
    }

}
