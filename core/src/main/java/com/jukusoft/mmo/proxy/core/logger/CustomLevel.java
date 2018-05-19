package com.jukusoft.mmo.proxy.core.logger;

import java.util.logging.Level;

public class CustomLevel extends Level {

    public CustomLevel(String name, int value, String resourceBundleName) {
        super(name, value, resourceBundleName);
    }

    public CustomLevel(String name, int value) {
        super(name, value);
    }

}
