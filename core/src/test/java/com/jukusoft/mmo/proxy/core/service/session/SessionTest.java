package com.jukusoft.mmo.proxy.core.service.session;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SessionTest {

    @Test
    public void testConstructor () {
        Session session = new Session("test");
        assertEquals(true, session.getSessionID().equals("test"));
    }

}
