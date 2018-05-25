package com.jukusoft.mmo.proxy.database.firewall;

import com.jukusoft.mmo.proxy.core.service.firewall.IFirewall;

public class DummyFirewall implements IFirewall {

    @Override
    public boolean isBlacklisted(String ip) {
        return false;
    }

}
