package com.jukusoft.mmo.proxy.database.login;

import com.jukusoft.mmo.proxy.core.login.LoginService;
import org.ini4j.Ini;
import org.ini4j.Profile;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapContext;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

public class LDAPLogin implements LoginService {

    //https://cweiske.de/tagebuch/ldap-server-travis.htm

    //https://www.hascode.com/2016/07/ldap-testing-with-java-apacheds-vs-embedded-ldap-junit/

    protected String host = "";
    protected int port = 389;
    protected boolean ssl = false;

    public LDAPLogin () {
        //
    }

    public void loadConfig (File configFile) throws IOException {
        Ini ini = new Ini(configFile);
        Profile.Section section = ini.get("LDAP");

        this.host = section.get("host");
        this.port = getInt(section, "port");
        this.ssl = getBoolean(section, "ssl");
    }

    protected int getInt (Profile.Section section, String key) {
        return Integer.parseInt(section.get(key));
    }

    protected boolean getBoolean (Profile.Section section, String key) {
        return Boolean.parseBoolean(section.get(key));
    }

    @Override
    public int login(String username, String password) {
        // setup the environment
        Hashtable<String, String> env = new Hashtable<String,String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, String.format("ldap://%s:%s", this.host, this.port));
        env.put("com.sun.jndi.ldap.connect.pool", "true");

        if (this.ssl) {
            //activate ssl
            env.put(Context.SECURITY_PROTOCOL, "ssl");
        }

        //TODO: generate userDN
        String userDn = username;

        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, userDn);//example: "cn=S. User, ou=NewHires, o=JNDITutorial"
        env.put(Context.SECURITY_CREDENTIALS, password);
        DirContext context = null;

        // do the ldap bind (validate username and password
        boolean loggedIn;
        try {
            context = new InitialDirContext(env);
            loggedIn = true;
        } catch (NamingException e) {
            loggedIn = false;
            return 0;
        }

        //get userID from mySQL database or create user

        return 1;
    }

    /*private User getUserBasicAttributes(String username, LdapContext ctx) {
        User user=null;
        try {

            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String[] attrIDs = { "distinguishedName",
                    "sn",
                    "givenname",
                    "mail",
                    "telephonenumber"};
            constraints.setReturningAttributes(attrIDs);
            //First input parameter is search bas, it can be "CN=Users,DC=YourDomain,DC=com"
            //Second Attribute can be uid=username
            NamingEnumeration answer = ctx.search("DC=YourDomain,DC=com", "sAMAccountName="
                    + username, constraints);
            if (answer.hasMore()) {
                Attributes attrs = ((SearchResult) answer.next()).getAttributes();
                System.out.println("distinguishedName "+ attrs.get("distinguishedName"));
                System.out.println("givenname "+ attrs.get("givenname"));
                System.out.println("sn "+ attrs.get("sn"));
                System.out.println("mail "+ attrs.get("mail"));
                System.out.println("telephonenumber "+ attrs.get("telephonenumber"));
            }else{
                throw new Exception("Invalid User");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return user;
    }*/

}
