package com.jukusoft.mmo.proxy.database.login;

import org.apache.directory.server.annotations.CreateLdapServer;
import org.apache.directory.server.annotations.CreateTransport;
import org.apache.directory.server.core.annotations.ApplyLdifFiles;
import org.apache.directory.server.core.annotations.CreateDS;
import org.apache.directory.server.core.annotations.CreatePartition;
import org.apache.directory.server.core.integ.AbstractLdapTestUnit;
import org.apache.directory.server.core.integ.FrameworkRunner;
import org.apache.directory.server.integ.ServerIntegrationUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.SortControl;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Test using apacheds
 */
@RunWith(FrameworkRunner.class)
@CreateLdapServer(transports = { @CreateTransport(protocol = "LDAP") })
@CreateDS(allowAnonAccess = true, partitions = {
        @CreatePartition(name = "Example Partition", suffix = "dc=example,dc=com") })
@ApplyLdifFiles("users-import.ldif")
public class LDAPLoginTest extends AbstractLdapTestUnit {

    //https://github.com/zapodot/embedded-ldap-junit

    //https://www.netiq.com/communities/cool-solutions/cool_tools/easily-generate-ldif-file-testing/

    //http://ldapwiki.com/wiki/LDIF%20Generator

    //https://gist.github.com/hasithaa/9597687

    //https://theholyjava.wordpress.com/2010/05/05/mocking-out-ldapjndi-in-unit-tests/

    //https://www.hascode.com/2016/07/ldap-testing-with-java-apacheds-vs-embedded-ldap-junit/

    public static final String DOMAIN_DSN = "dc=example,dc=com";

    /*@Rule
    public EmbeddedLdapRule embeddedLdapRule = EmbeddedLdapRuleBuilder
            .newInstance()
            //.withSchema("schema/core.schema", "schema/cosine.schema", "schema/nis.schema", "schema/inetorgperson.schema")
            .usingDomainDsn("dc=example,dc=com")
            .importingLdifs("example.ldif")
            .build();*/

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

    /*@Test
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
    }*/

    @Test
    public void shouldFindAllPersons() throws Exception {
        //https://www.hascode.com/2016/07/ldap-testing-with-java-apacheds-vs-embedded-ldap-junit/

        LdapContext ctx = (LdapContext) ServerIntegrationUtils.getWiredContext(ldapServer, null)
                .lookup("ou=Users,dc=example,dc=com");

        // we want a sorted result, based on the canonical name
        ctx.setRequestControls(new Control[] { new SortControl("cn", Control.CRITICAL) });

        NamingEnumeration<SearchResult> res = ctx.search("", "(objectClass=person)", new SearchControls());
        assertEquals(true, res.hasMore());

        assertEquals("cn=John Steinbeck", res.next().getName());
        assertEquals("cn=Micha Kops", res.next().getName());
        assertEquals("cn=Santa Claus", res.next().getName());

    }

}
