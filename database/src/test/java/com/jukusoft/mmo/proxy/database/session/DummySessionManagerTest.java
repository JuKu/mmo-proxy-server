package com.jukusoft.mmo.proxy.database.session;

import com.jukusoft.mmo.proxy.core.service.session.ISessionManager;
import com.jukusoft.mmo.proxy.core.service.session.Session;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DummySessionManagerTest {

    @Test
    public void testConstructor () {
        new DummySessionManager();
    }

    @Test
    public void testCreateSession () {
        ISessionManager sessionManager = new DummySessionManager();
        Session session = sessionManager.createSession("127.0.0.1", 20);
        assertNotNull(session);
        assertEquals(false, session.getSessionID().isEmpty());

        //close session
        sessionManager.closeSession(session.getSessionID());
    }

}
