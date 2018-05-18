package com.jukusoft.mmo.proxy.database;

import com.jukusoft.mmo.proxy.database.config.MySQLConfig;
import org.junit.Test;

public class DatabaseUpgraderTest {

    @Test
    public void testConstructor () {
        MySQLConfig mySQLConfig = new MySQLConfig();
        new DatabaseUpgrader(mySQLConfig);
    }

}
