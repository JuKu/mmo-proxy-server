package com.jukusoft.mmo.proxy.core.service.connection;

import com.jukusoft.mmo.proxy.core.message.MessageReceiver;
import io.vertx.core.buffer.Buffer;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ClientConnectionTest {

    @Test
    public void testConstructor () {
        new ClientConnection();
    }

    @Test
    public void testInit () {
        createConn();
    }

    protected ClientConnection createConn () {
        IConnectionManager manager = Mockito.mock(IConnectionManager.class);

        ClientConnection conn = new ClientConnection();
        conn.init(manager);

        return conn;
    }

    @Test (expected = NullPointerException.class)
    public void testReceiveNullContent () {
        ClientConnection conn = createConn();

        conn.receive(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testReceiveEmptyContent () {
        ClientConnection conn = createConn();

        conn.receive(Buffer.buffer());
    }

    @Test
    public void testReceive () {
        ClientConnection conn = createConn();

        //create a message with content
        Buffer content = Buffer.buffer().setByte(0, (byte) 0x01).setByte(1, (byte) 0x00).setShort(2, (short) 1).setInt(4, 10).setInt(8, 2);
        conn.receive(content);
    }

    @Test
    public void testReceiveSpecialType () {
        ClientConnection conn = createConn();

        //create a message with content
        Buffer content = Buffer.buffer().setByte(0, (byte) 0x02).setByte(1, (byte) 0x00).setShort(2, (short) 1).setInt(4, 10).setInt(8, 2);
        conn.receive(content);
    }

    @Test
    public void testReceiveForwardType () {
        ClientConnection conn = createConn();

        //create a message with content
        Buffer content = Buffer.buffer().setByte(0, (byte) 0x03).setByte(1, (byte) 0x00).setShort(2, (short) 1).setInt(4, 10).setInt(8, 2);
        conn.receive(content);
    }

    @Test
    public void testSetCID () {
        ClientConnection conn = createConn();

        Buffer content = Buffer.buffer().setByte(0, (byte) 0x01).setByte(1, (byte) 0x00).setShort(2, (short) 1).setInt(4, 10);

        assertEquals(10, content.getInt(4));

        conn.setCID(content);
        assertEquals(-1, content.getInt(4));

        conn.cid = 20;
        conn.setCID(content);
        assertEquals(20, content.getInt(4));
    }

    @Test (expected = NullPointerException.class)
    public void testSetNullReceiver () {
        ClientConnection conn = createConn();

        conn.setReceiver(null);
    }

    @Test
    public void testSetReceiver () {
        ClientConnection conn = createConn();

        conn.setReceiver(new MessageReceiver<Buffer>() {
            @Override
            public void receive(Buffer buffer) {
                //
            }
        });
    }

    @Test
    public void testClose () {
        ClientConnection conn = createConn();
        conn.close();
    }

    @Test (expected = NullPointerException.class)
    public void testHandleNullProxyMsg () {
        ClientConnection conn = createConn();
        conn.handleProxyMsg(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testHandleEmptyProxyMsg () {
        ClientConnection conn = createConn();
        conn.handleProxyMsg(Buffer.buffer());
    }

    @Test
    public void testGetState () {
        ClientConnection conn = createConn();
        assertNotNull(conn.getState());
    }

    @Test
    public void testGetAndSetConnID () {
        ClientConnection conn = createConn();

        assertEquals(0, conn.getConnID());

        conn.setConnID(10);
        assertEquals(10, conn.getConnID());
    }

    @Test
    public void testIsLoggedIn () {
        ClientConnection conn = createConn();
        assertEquals(false, conn.isLoggedIn());
    }

    @Test
    public void testCountBackendConnections () {
        ClientConnection conn = createConn();
        assertEquals(0, conn.countBackendConnections());
    }

}
