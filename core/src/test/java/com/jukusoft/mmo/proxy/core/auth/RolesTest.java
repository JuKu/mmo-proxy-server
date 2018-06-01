package com.jukusoft.mmo.proxy.core.auth;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RolesTest {

    @Test
    public void testGetValue () {
        assertEquals(0, Roles.GAMEMASTER.getValue());
    }

    @Test
    public void testCountRoles () {
        assertEquals(true, Roles.countRoles() > 0);
    }

}
