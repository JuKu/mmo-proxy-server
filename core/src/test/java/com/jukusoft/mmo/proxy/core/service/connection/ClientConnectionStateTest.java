package com.jukusoft.mmo.proxy.core.service.connection;

import com.jukusoft.mmo.proxy.core.auth.Roles;
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

    @Test
    public void testHasRole () {
        ConnectionState state = new ConnectionState();
        assertEquals(false, state.hasRole(Roles.GAMEMASTER));
        assertEquals(false, state.hasRole(Roles.SUPPORT));
        assertEquals(false, state.hasRole(Roles.DEVELOPER));
        assertEquals(false, state.hasRole(Roles.ADMINISTRATOR));
        assertEquals(false, state.hasRole(Roles.QA_TEAM));

        //set role
        state.setRole(Roles.GAMEMASTER);
        assertEquals(true, state.hasRole(Roles.GAMEMASTER));

        //unset role
        state.unsetRole(Roles.GAMEMASTER);
        assertEquals(false, state.hasRole(Roles.GAMEMASTER));

        //set role
        state.setRole(Roles.DEVELOPER);
        assertEquals(true, state.hasRole(Roles.DEVELOPER));
    }

    @Test
    public void testIsCharacterSelected () {
        ConnectionState state = new ConnectionState();
        assertEquals(false, state.isCharacterSelected());

        state.setCID(10);
        assertEquals(true, state.isCharacterSelected());
    }

}
