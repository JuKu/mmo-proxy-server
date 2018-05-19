package com.jukusoft.mmo.proxy.core.logger;

import org.junit.Test;

public class CustomLevelTest {

    @Test
    public void testConstructor () {
        new CustomLevel("WARNING", 900);
    }

    @Test
    public void testConstructor1 () {
        new CustomLevel("WARNING", 900, "sun.util.logging.resources.logging");
    }

}
