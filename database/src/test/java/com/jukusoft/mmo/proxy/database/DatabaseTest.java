package com.jukusoft.mmo.proxy.database;

import com.jukusoft.mmo.proxy.database.config.MySQLConfig;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DatabaseTest {

    @Test
    public void testConstructor () {
        new Database();
    }

    @Test
    public void testInit () throws IOException, SQLException {
        //load test mysql configuration
        MySQLConfig mySQLConfig = createConfig();

        //initialize database
        Database.init(mySQLConfig);

        assertNotNull(Database.getDataSource());
        assertNotNull(Database.getConnection());

        //close database connection
        Database.close();
    }

    @Test
    public void testReplacePrefix () throws IOException {
        //load test mysql configuration
        MySQLConfig mySQLConfig = createConfig();

        //initialize database
        Database.init(mySQLConfig);

        String query = Database.replacePrefix("SELECT * FROM `{prefix}users`; ");
        assertEquals("SELECT * FROM `mmo_users`; ", query);
    }

    protected MySQLConfig createConfig () throws IOException {
        MySQLConfig mySQLConfig = new MySQLConfig();

        //https://docs.travis-ci.com/user/database-setup/#MySQL

        if (new File("../config/mysql.cfg").exists()) {
            mySQLConfig.load(new File("../config/mysql.cfg"));
        } else {
            mySQLConfig.load(new File("../config/tests/travis.mysql.cfg"));
        }

        return mySQLConfig;
    }

}
