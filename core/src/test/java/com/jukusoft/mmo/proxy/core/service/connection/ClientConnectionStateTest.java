package com.jukusoft.mmo.proxy.core.service.connection;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ClientConnectionStateTest {

    @Test
    public void testConstructor () {
        new ConnectionState();
    }

    @Test
    public void testGetterAndSetter () {
        ConnectionState state = new ConnectionState();

        //user and character ID wasnt set before
        assertEquals(-1, state.getUserID());
        assertEquals(-1, state.getCID());
        assertEquals(false, state.isLoggedIn());

        state.setUserID(1);
        assertEquals(1, state.getUserID());
        assertEquals(true, state.isLoggedIn());

        state.setCID(2);
        assertEquals(2, state.getCID());
    }

}
