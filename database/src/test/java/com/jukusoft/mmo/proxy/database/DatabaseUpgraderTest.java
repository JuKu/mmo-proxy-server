package com.jukusoft.mmo.proxy.database;

import com.jukusoft.mmo.proxy.database.config.MySQLConfig;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.*;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DatabaseUpgraderTest {

    @Test
    public void testConstructor () {
        MySQLConfig mySQLConfig = new MySQLConfig();
        new DatabaseUpgrader(mySQLConfig);
    }

    @Test
    public void testConstructor1 () {
        MySQLConfig mySQLConfig = new MySQLConfig();
        mySQLConfig.setPrefix("");
        new DatabaseUpgrader(mySQLConfig);
    }

    @Test
    public void testConnect () throws IOException {
        //https://docs.travis-ci.com/user/database-setup/#MySQL

        MySQLConfig mySQLConfig = createConfig();

        DatabaseUpgrader databaseUpgrader = new DatabaseUpgrader(mySQLConfig);
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

    @Test
    public void testGetInfo () throws IOException {
        MySQLConfig mySQLConfig = createConfig();
        DatabaseUpgrader databaseUpgrader = new DatabaseUpgrader(mySQLConfig);

        assertNotNull(databaseUpgrader.getInfo());
    }

    @Test
    public void testGetInfo1 () throws IOException {
        MySQLConfig mySQLConfig = createConfig();
        DatabaseUpgrader databaseUpgrader = new DatabaseUpgrader(mySQLConfig);

        databaseUpgrader.flyway = Mockito.mock(Flyway.class);
        Mockito.when(databaseUpgrader.flyway.info()).thenReturn(new MigrationInfoService() {
            @Override
            public MigrationInfo[] all() {
                return new MigrationInfo[] {
                    new MigrationInfo() {
                        @Override
                        public int compareTo(MigrationInfo o) {
                            return 0;
                        }

                        @Override
                        public MigrationType getType() {
                            return MigrationType.JDBC;
                        }

                        @Override
                        public Integer getChecksum() {
                            return 10;
                        }

                        @Override
                        public MigrationVersion getVersion() {
                            return MigrationVersion.CURRENT;
                        }

                        @Override
                        public String getDescription() {
                            return "description";
                        }

                        @Override
                        public String getScript() {
                            return "script";
                        }

                        @Override
                        public MigrationState getState() {
                            return MigrationState.SUCCESS;
                        }

                        @Override
                        public Date getInstalledOn() {
                            return new Date();
                        }

                        @Override
                        public String getInstalledBy() {
                            return "user";
                        }

                        @Override
                        public Integer getInstalledRank() {
                            return 10;
                        }

                        @Override
                        public Integer getExecutionTime() {
                            return 10;
                        }
                    }
                };
            }

            @Override
            public MigrationInfo current() {
                return null;
            }

            @Override
            public MigrationInfo[] pending() {
                return new MigrationInfo[0];
            }

            @Override
            public MigrationInfo[] applied() {
                return new MigrationInfo[0];
            }
        });

        assertNotNull(databaseUpgrader.getInfo());
    }

    @Test
    public void testMigrate () throws IOException {
        MySQLConfig mySQLConfig = createConfig();

        DatabaseUpgrader databaseUpgrader = new DatabaseUpgrader(mySQLConfig);
        databaseUpgrader.migrate();

    }

}
