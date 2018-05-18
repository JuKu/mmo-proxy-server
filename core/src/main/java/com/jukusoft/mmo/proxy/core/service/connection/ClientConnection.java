package com.jukusoft.mmo.proxy.core.service.connection;

import com.jukusoft.mmo.proxy.core.config.Config;
import com.jukusoft.mmo.proxy.core.logger.MMOLogger;
import com.jukusoft.mmo.proxy.core.message.MessageReceiver;
import com.jukusoft.mmo.proxy.core.utils.ByteUtils;
import io.vertx.core.buffer.Buffer;

public class ClientConnection {

    protected final ConnectionState state = new ConnectionState();
    protected long connID = 0;
    protected IConnectionManager manager = null;

    protected int abusedMsgCount = 0;

    //flag, if user is logged in
    protected volatile boolean loggedIn = false;

    //selected character id
    protected volatile int cid = -1;

    //listener for TCPFrontend to send messages back to client
    protected MessageReceiver<Buffer> receiver = null;

    //game server connection
    protected GSConnection gsConn = null;

    public ClientConnection() {
        //
    }

    public void init (IConnectionManager manager) {
        this.manager = manager;
    }

    /**
     * method is called, if client has sended a package from client to proxy server
     * proxy has to redirect this package to current sector server
    */
    public void receive (Buffer content) {
        //check, if message type is allowed from client
        byte type = content.getByte(0);

        //check, if type should be sended into cluster
        if (Config.MSG_SPECIAL_PROXY_TYPES[type]) {
            handleProxyMsg(content);
            return;
        }

        //set character id
        this.setCID(content);

        if (Config.MSG_REDIRECT_TYPES[type]) {
            //first check, if user is logged in

            if (!this.loggedIn) {
                //drop message
                MMOLogger.warn(Config.LOG_TAG_CLIENT_CONNECTION, "Dropped message 0x" + ByteUtils.byteToHex(type) + " because client (" + this.connID + ") isnt logged in!");

                return;
            }

            //redirect message to current game server
            if (this.gsConn == null || !this.gsConn.isOpened()) {
                //drop message
                MMOLogger.warn(Config.LOG_TAG_CLIENT_CONNECTION, "Dropped message 0x" + ByteUtils.byteToHex(type) + " because no game server connection exists!");

                return;
            }

            //redirect message to game server
            this.gsConn.send(content);

            return;
        }

        MMOLogger.warn(Config.LOG_TAG_CLIENT_CONNECTION, "no handler for message type 0x" + ByteUtils.byteToHex(type) + " found.");
    }

    protected final Buffer setCID (Buffer content) {
        //set character id
        content.setInt(Config.MSG_HEADER_CID_POS, this.cid);

        return content;
    }

    public void setReceiver (MessageReceiver<Buffer> receiver) {
        this.receiver = receiver;
    }

    public void close () {
        //
    }

    /**
    * handle special messages which arent redirected to game server, e.q. login messages
    */
    protected void handleProxyMsg (Buffer content) {
        //
    }

    public ConnectionState getState () {
        return this.state;
    }

    public long getConnID () {
        return this.connID;
    }

    public void setConnID (final long connID) {
        this.connID = connID;
    }

    public boolean isLoggedIn () {
        return this.loggedIn;
    }

    public int countBackendConnections () {
        return 0;
    }

}
