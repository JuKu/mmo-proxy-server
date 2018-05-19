package com.jukusoft.mmo.proxy.database;

import com.jukusoft.mmo.proxy.database.config.MySQLConfig;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.flywaydb.core.Flyway;

public class DatabaseUpgrader {

    protected Flyway flyway = null;

    public DatabaseUpgrader (MySQLConfig mySQLConfig) {
        //create the Flyway instance
        this.flyway = new Flyway();

        //https://github.com/timander/flyway-example

        //https://scalified.com/2018/01/17/java-backend-database-migration-flyway/

        //https://github.com/timander/flyway-example/blob/master/flyway.conf

        this.flyway.setDataSource("jdbc:mysql://" + mySQLConfig.getHost() + ":" + mySQLConfig.getPort() + "/" + mySQLConfig.getDatabase() + "?autoreconnect=true", mySQLConfig.getUser(), mySQLConfig.getPassword());
        //this.flyway.setDataSource(new MysqlDataSource());

        //set encoding
        this.flyway.setEncoding("utf-8");

        //this.flyway.setLocations("filesystem:src/main/resources/sql/migrations");

        //this.flyway.setDataSource();
    }

    public void migrate () {
        //create or upgrade database schema
        this.flyway.migrate();
    }

}
