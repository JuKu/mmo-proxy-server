package com.jukusoft.mmo.proxy.backend;

import com.jukusoft.mmo.proxy.core.service.connection.ClientConnection;
import com.jukusoft.mmo.proxy.core.service.connection.IConnectionManager;
import io.vertx.core.Vertx;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

public class ConnectionManagerImplTest {

    protected static Vertx vertx = null;

    @BeforeClass
    public static void beforeClass () {
        vertx = Vertx.vertx();
    }

    @AfterClass
    public static void afterClass () {
        vertx.close();
        vertx = null;
    }

    @Test
    public void testConstructor () {
        new ConnectionManagerImpl(vertx);
    }

    @Test
    public void testAddAndRemoveConnection () {
        IConnectionManager manager = new ConnectionManagerImpl(vertx);

        assertEquals(0, manager.countOpenFrontendConnections());
        assertEquals(0, manager.countOpenBackendConnections());

        //add connection
        ClientConnection conn = Mockito.mock(ClientConnection.class);
        manager.addConnection("127.0.0.1", 1234, conn);

        assertEquals(1, manager.countOpenFrontendConnections());
        assertEquals(0, manager.countOpenBackendConnections());

        //remove connection
        manager.removeConnection(conn);
    }

}
