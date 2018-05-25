package com.jukusoft.mmo.proxy.database.firewall;

import com.jukusoft.mmo.proxy.core.service.firewall.IFirewall;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DummyFirewallTest {

    @Test
    public void testConstructor () {
        new DummyFirewall();
    }

    @Test
    public void testIsBlacklisted () {
        IFirewall firewall = new DummyFirewall();
        assertEquals(false, firewall.isBlacklisted("127.0.0.1"));
    }

}
