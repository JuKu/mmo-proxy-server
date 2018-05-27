package com.jukusoft.mmo.proxy.core.service.connection;

import com.jukusoft.mmo.proxy.core.config.Config;
import com.jukusoft.mmo.proxy.core.message.MessageReceiver;
import com.jukusoft.mmo.proxy.core.utils.MessageUtils;
import io.vertx.core.Handler;
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
        conn.init(manager, Mockito.mock(GSConnectionManager.class), "127.0.0.1");

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
    public void testReceiveForwardType1 () {
        ClientConnection conn = createConn();

        //set logged in
        conn.state.setUserID(1);

        //create a message with content
        Buffer content = Buffer.buffer().setByte(0, (byte) 0x03).setByte(1, (byte) 0x00).setShort(2, (short) 1).setInt(4, 10).setInt(8, 2);
        conn.receive(content);
    }

    @Test
    public void testReceiveForwardType2 () {
        ClientConnection conn = createConn();

        //set logged in
        conn.state.setUserID(1);

        conn.gsConn = new GSConnection() {
            @Override
            public void send(Buffer content) {

            }

            @Override
            public void setReceiver(MessageReceiver<Buffer> receiver) {

            }

            @Override
            public void setCloseHandler(Handler<Void> handler) {

            }

            @Override
            public boolean isOpened() {
                return false;
            }

            @Override
            public void close() {

            }
        };

        //create a message with content
        Buffer content = Buffer.buffer().setByte(0, (byte) 0x03).setByte(1, (byte) 0x00).setShort(2, (short) 1).setInt(4, 10).setInt(8, 2);
        conn.receive(content);
    }

    @Test
    public void testReceiveForwardType3 () {
        ClientConnection conn = createConn();

        //set logged in
        conn.state.setUserID(1);

        conn.gsConn = new GSConnection() {
            @Override
            public void send(Buffer content) {

            }

            @Override
            public void setReceiver(MessageReceiver<Buffer> receiver) {

            }

            @Override
            public void setCloseHandler(Handler<Void> handler) {

            }

            @Override
            public boolean isOpened() {
                return true;
            }

            @Override
            public void close() {

            }
        };

        //create a message with content
        Buffer content = Buffer.buffer().setByte(0, (byte) 0x03).setByte(1, (byte) 0x00).setShort(2, (short) 1).setInt(4, 10).setInt(8, 2);
        conn.receive(content);
    }

    @Test
    public void testReceiveUnknownType () {
        ClientConnection conn = createConn();

        //set logged in
        conn.state.setUserID(1);

        //create a message with content
        Buffer content = Buffer.buffer().setByte(0, (byte) 0xFF).setByte(1, (byte) 0x00).setShort(2, (short) 1).setInt(4, 10).setInt(8, 2);
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

    @Test
    public void testClose1 () {
        ClientConnection conn = createConn();
        conn.gsConn = Mockito.mock(GSConnection.class);
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

    @Test (expected = NullPointerException.class)
    public void testHandleInternalNullMessage () {
        ClientConnection conn = createConn();
        conn.handleInternalMessage(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testHandleInternalEmptyMessage () {
        ClientConnection conn = createConn();
        conn.handleInternalMessage(Buffer.buffer());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testHandleInternalMessage () {
        ClientConnection conn = createConn();

        //use unsupported type
        Buffer content = MessageUtils.createMsg((byte) 0xFF, (byte) 0x01, 1);
        content.setInt(Config.MSG_BODY_OFFSET, 10);

        conn.handleInternalMessage(content);
    }

    @Test
    public void testHandleInternalMessage1 () {
        ClientConnection conn = createConn();

        //use unsupported type
        Buffer content = MessageUtils.createMsg((byte) 0x01, (byte) 0x01, 1);
        content.setInt(Config.MSG_BODY_OFFSET, 10);

        conn.handleInternalMessage(content);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testOpenGSConnection () {
        ClientConnection conn = createConn();
        conn.openGSConnection(-1, 1, 1);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testOpenGSConnection1 () {
        ClientConnection conn = createConn();
        conn.openGSConnection(0, 1, 1);
    }

    @Test
    public void testOpenGSConnection2 () {
        ClientConnection conn = createConn();
        conn.openGSConnection(1, 1, 1);
    }

    @Test
    public void testOpenGSConnection3 () {
        ClientConnection conn = createConn();
        conn.gsConn = Mockito.mock(GSConnection.class);
        conn.openGSConnection(1, 1, 1);
    }

    @Test
    public void testOpenGSConnection4 () {
        ClientConnection conn = createConn();
        conn.gsConn = new GSConnection() {
            @Override
            public void send(Buffer content) {

            }

            @Override
            public void setReceiver(MessageReceiver<Buffer> receiver) {

            }

            @Override
            public void setCloseHandler(Handler<Void> handler) {

            }

            @Override
            public boolean isOpened() {
                return true;
            }

            @Override
            public void close() {

            }
        };

        conn.openGSConnection(1, 1, 1);
    }

    @Test
    public void testOpenGSConnection5 () {
        ClientConnection conn = createConn();
        conn.gsManager = new GSConnectionManager() {
            @Override
            public void createConnection(int sectorID, Handler<GSConnection> handler) {
                handler.handle(null);
            }
        };

        conn.openGSConnection(1, 1, 1);
    }

    @Test
    public void testOpenGSConnection6 () {
        ClientConnection conn = createConn();
        conn.gsManager = new GSConnectionManager() {
            @Override
            public void createConnection(int sectorID, Handler<GSConnection> handler) {
                handler.handle(new GSConnection() {
                    @Override
                    public void send(Buffer content) {

                    }

                    @Override
                    public void setReceiver(MessageReceiver<Buffer> receiver) {

                    }

                    @Override
                    public void setCloseHandler(Handler<Void> handler) {

                    }

                    @Override
                    public boolean isOpened() {
                        return false;
                    }

                    @Override
                    public void close() {

                    }
                });
            }
        };

        conn.openGSConnection(1, 1, 1);
    }

    @Test
    public void testOpenGSConnection7 () throws InterruptedException {
        ClientConnection conn = createConn();
        conn.gsManager = new GSConnectionManager() {
            @Override
            public void createConnection(int sectorID, Handler<GSConnection> handler) {
                handler.handle(new GSConnection() {
                    @Override
                    public void send(Buffer content) {

                    }

                    @Override
                    public void setReceiver(MessageReceiver<Buffer> receiver) {

                    }

                    @Override
                    public void setCloseHandler(Handler<Void> handler) {

                    }

                    @Override
                    public boolean isOpened() {
                        return true;
                    }

                    @Override
                    public void close() {

                    }
                });
            }
        };

        conn.openGSConnection(1, 1, 1);
    }

}
