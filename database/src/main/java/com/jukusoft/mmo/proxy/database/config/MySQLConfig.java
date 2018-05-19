package com.jukusoft.mmo.proxy.database.config;

import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;

public class MySQLConfig {

    protected String host = "locahost";
    protected int port = 3306;
    protected String database = "";
    protected String user = "";
    protected String password = "";
    protected String prefix = "";

    public MySQLConfig () {
        //
    }

    public void load (File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file cannot be null.");
        }

        if (!file.exists()) {
            throw new IllegalStateException("mysql config file doesnt exists: " + file.getAbsolutePath());
        }

        Ini ini = new Ini(file);
        Profile.Section section = ini.get("MySQL");

        this.host = section.getOrDefault("host", "locahost");
        this.port = getInt(section, "port");
        this.database = section.get("database");
        this.user = section.getOrDefault("user", "");
        this.password = section.get("password");
        this.prefix = section.getOrDefault("prefix", "");
    }

    protected int getInt (Profile.Section section, String key) {
        return Integer.parseInt(section.get(key));
    }

    public String getHost () {
        return this.host;
    }

    public int getPort () {
        return this.port;
    }

    public String getDatabase () {
        return this.database;
    }

    public String getUser () {
        return this.user;
    }

    public String getPassword () {
        return this.password;
    }

    public String getPrefix () {
        return this.prefix;
    }

}
