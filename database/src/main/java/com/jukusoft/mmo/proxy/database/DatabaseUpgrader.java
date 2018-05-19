package com.jukusoft.mmo.proxy.database;

import com.jukusoft.mmo.proxy.database.config.MySQLConfig;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.MigrationInfoService;

public class DatabaseUpgrader {

    protected Flyway flyway = null;

    public DatabaseUpgrader (MySQLConfig mySQLConfig) {
        //create the Flyway instance
        this.flyway = new Flyway();

        //https://github.com/timander/flyway-example

        //https://scalified.com/2018/01/17/java-backend-database-migration-flyway/

        //https://github.com/timander/flyway-example/blob/master/flyway.conf

        //https://www.programcreek.com/java-api-examples/index.php?api=com.googlecode.flyway.core.Flyway

        //http://www.liquibase.org/

        this.flyway.setDataSource("jdbc:mysql://" + mySQLConfig.getHost() + ":" + mySQLConfig.getPort() + "/" + mySQLConfig.getDatabase() + "?autoreconnect=true", mySQLConfig.getUser(), mySQLConfig.getPassword());

        //set encoding
        this.flyway.setEncoding("utf-8");
    }

    public void migrate () {
        this.flyway.validate();

        //create or upgrade database schema
        this.flyway.migrate();
    }

    public String getInfo () {
        MigrationInfoService infoService = this.flyway.info();

        String s = "";

        for (MigrationInfo info : infoService.all()) {
            s += " - " + info.getDescription() + ", script: " + info.getScript() + ", state: " + info.getState() + ", version: " + info.getVersion() + "\n";
        }

        return s;
    }

}
