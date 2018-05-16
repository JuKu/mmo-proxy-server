package com.jukusoft.mmo.proxy.core;

import com.jukusoft.mmo.proxy.core.service.IService;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyServerTest {

    @Test
    @DisplayName("Test constructor of proxy server")
    public void testConstructor () {
        new ProxyServer();
    }

}
