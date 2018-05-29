package com.jukusoft.mmo.proxy.database.login;

import com.jukusoft.mmo.proxy.core.logger.MMOLogger;
import com.jukusoft.mmo.proxy.core.login.LoginService;
import com.jukusoft.mmo.proxy.database.Database;
import org.ini4j.Ini;
import org.ini4j.Profile;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

public class LDAPLogin implements LoginService {

    //https://cweiske.de/tagebuch/ldap-server-travis.htm

    //https://www.hascode.com/2016/07/ldap-testing-with-java-apacheds-vs-embedded-ldap-junit/

    protected String host = "";
    protected int port = 389;
    protected boolean ssl = false;

    protected String user_prefix = "";
    protected String user_suffix = "";

    protected static final String INSERT_QUERY = String.format("INSERT INTO `mmo_users` (   `userID`, `username`, `ip`, `online`, `last_online`, `activated`) VALUES (   NULL, ?, ?, '1', CURRENT_TIMESTAMP, '1') ON DUPLICATE KEY UPDATE `ip` = ?, `online` = '1', `last_online` = NOW();");
    protected static final String SELECT_QUERY = String.format("SELECT * FROM `mmo_users` WHERE `username` = ?; ");

    public LDAPLogin () {
        //
    }

    public void loadConfig (File configFile) throws IOException {
        Ini ini = new Ini(configFile);
        Profile.Section section = ini.get("LDAP");

        this.host = section.get("host");
        this.port = getInt(section, "port");
        this.ssl = getBoolean(section, "ssl");
        this.user_prefix = section.get("user_prefix");
        this.user_suffix = section.get("user_suffix");
    }

    protected int getInt (Profile.Section section, String key) {
        return Integer.parseInt(section.get(key));
    }

    protected boolean getBoolean (Profile.Section section, String key) {
        return Boolean.parseBoolean(section.get(key));
    }

    @Override
    public int login(String username, String password, String ip) {
        // setup the environment
        Hashtable<String, String> env = new Hashtable<String,String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, String.format("ldap://%s:%s", this.host, this.port));
        env.put("com.sun.jndi.ldap.connect.pool", "true");

        if (this.ssl) {
            //activate ssl
            env.put(Context.SECURITY_PROTOCOL, "ssl");
        }

        //generate userDN
        String userDn = this.user_prefix + username.replace(",", "") + this.user_suffix;
        MMOLogger.info("LDAPLogin", "ldap server: " + host + ":" + port);
        MMOLogger.info("LDAPLogin", "try to login ldap user: " + userDn);

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

        MMOLogger.info("LDAPLogin", "authorization successful for user '" + userDn + "'!");

        try (Connection conn = Database.getConnection()) {
            MMOLogger.info("LDAPLogin", "execute sql query: " + INSERT_QUERY);

            //insert user, if absent
            PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY);
            stmt.setString(1, username);
            stmt.setString(2, ip);
            stmt.setString(3, ip);
            stmt.execute();

            //get userID
            stmt = conn.prepareStatement(SELECT_QUERY);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                //get first element
                int userID = rs.getInt("userID");
                int activated = rs.getInt("activated");

                //check, if user is activated
                if (activated != 1) {
                    MMOLogger.warn("LDAPLogin", "user '" + username + "' exists but is not activated.");
                    return 0;
                }

                return userID;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }

        return 0;
    }

    //http://www.deepakgaikwad.net/index.php/2009/09/24/retrieve-basic-user-attributes-from-active-directory-using-ldap-in-java.html

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
