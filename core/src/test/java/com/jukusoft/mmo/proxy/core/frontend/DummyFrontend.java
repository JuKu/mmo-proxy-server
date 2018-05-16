package com.jukusoft.mmo.proxy.core.frontend;

import com.jukusoft.mmo.proxy.core.ProxyServer;

public class DummyFrontend implements IFrontend {

    @Override
    public String getName() {
        return "Dummy Frontend";
    }

    @Override
    public String getDescription() {
        return "Dummy Frontend";
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public void init(ProxyServer server) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

}
