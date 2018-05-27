package com.jukusoft.mmo.proxy.core.service.connection;

import com.jukusoft.mmo.proxy.core.config.Config;
import com.jukusoft.mmo.proxy.core.handler.MessageHandler;
import com.jukusoft.mmo.proxy.core.logger.MMOLogger;
import com.jukusoft.mmo.proxy.core.message.MessageReceiver;
import com.jukusoft.mmo.proxy.core.utils.ByteUtils;
import com.jukusoft.mmo.proxy.core.utils.EncryptionUtils;
import com.jukusoft.mmo.proxy.core.utils.MessageUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class ClientConnection {

    protected final ConnectionState state = new ConnectionState();
    protected long connID = 0;
    protected IConnectionManager manager = null;
    protected GSConnectionManager gsManager = null;

    protected int abusedMsgCount = 0;

    //selected character id
    protected volatile int cid = -1;

    //current sector id
    protected volatile int sectorID = 0;

    //listener for TCPFrontend to send messages back to client
    protected MessageReceiver<Buffer> receiver = null;

    //game server connection
    protected GSConnection gsConn = null;

    protected String ip = "";

    public ClientConnection() {
        //
    }

    public void init (IConnectionManager manager, GSConnectionManager gsManager, String ip) {
        this.manager = manager;
        this.gsManager = gsManager;
        this.ip = ip;
    }

    /**
     * method is called, if client has sended a package from client to proxy server
     * proxy has to redirect this package to current sector server
    */
    public void receive (Buffer content) {
        if (content == null) {
            throw new NullPointerException("content cannot be null.");
        }

        if (content.length() < 1) {
            throw new IllegalArgumentException("content cannot be empty.");
        }

        //check, if message type is allowed from client
        byte type = content.getByte(0);

        //check, if client is not allowed to send such message types
        if (Config.MSG_INTERNAL_TYPES[ByteUtils.byteToUnsignedInt(type)]) {
            //drop message
            MMOLogger.warn("ClientConnection", "Drop message, because client is not allowed to send such message type: 0x" + ByteUtils.byteToHex(type));

            return;
        }

        //check, if type should be sended into cluster
        if (Config.MSG_SPECIAL_PROXY_TYPES[ByteUtils.byteToUnsignedInt(type)]) {
            handleProxyMsg(content);
            return;
        }

        //set character id
        this.setCID(content);

        if (Config.MSG_REDIRECT_TYPES[ByteUtils.byteToUnsignedInt(type)]) {
            //first check, if user is logged in

            if (!this.isLoggedIn()) {
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

    protected void openGSConnection (int sectorID, float xPos, float yPos) {
        if (sectorID <= 0) {
            throw new IllegalArgumentException("sectorID has to be greater than 0.");
        }

        //first, close old connection, if neccessary
        if (this.gsConn != null && this.gsConn.isOpened()) {
            //send leave message
            Buffer content = MessageUtils.createMsg(Config.MSG_TYPE_GS, Config.MSG_EXTENDED_TYPE_LEAVE, this.cid);
            this.gsConn.send(content);

            //close connection
            this.gsConn.close();
            this.gsConn = null;
        }

        //open new connection
        this.gsManager.createConnection(sectorID, connection -> {
            if (connection == null) {
                //log error
                MMOLogger.fatal("error-500", "Couldnt open connection to game server with sectorID " + sectorID);

                //send error message to client
                Buffer content = MessageUtils.createErrorMsg(Config.MSG_EXTENDED_TYPE_INTERNAL_SERVER_ERROR, this.cid);
                this.receive(content);

                return;
            }

            //set new game server connection
            this.gsConn = connection;

            //add message handler
            this.gsConn.setReceiver(buffer -> {
                //get message type
                byte type = buffer.getByte(0);

                //check, if message has to be handled internal
                if (Config.MSG_INTERNAL_TYPES[ByteUtils.byteToUnsignedInt(type)]) {
                    handleInternalMessage(buffer);

                    return;
                }

                //send message back to client
                this.sendToClient(buffer);
            });

            //send join message
            Buffer content = MessageUtils.createMsg(Config.MSG_TYPE_GS, Config.MSG_EXTENDED_TYPE_JOIN, this.cid);
            content.setFloat(Config.MSG_BODY_OFFSET, xPos);
            content.setFloat(Config.MSG_BODY_OFFSET + 4, yPos);
            this.gsConn.send(content);
        });

        //set new sectorID
        this.sectorID = sectorID;
    }

    /**
    * handle internal message from game server
    */
    protected void handleInternalMessage (Buffer content) {
        if (content == null) {
            throw new NullPointerException("buffer cannot be null.");
        }

        if (content.length() < Config.MSG_HEADER_LENGTH) {
            throw new IllegalArgumentException("buffer doesnt contains full header");
        }

        //get type
        byte type = content.getByte(0);

        switch (type) {
            case 0x01:
                //TODO: add code here

                break;

            default:
                throw new IllegalArgumentException("internal message type 0x" + ByteUtils.byteToHex(type) + " is not supported yet.");
        }
    }

    public void setReceiver (MessageReceiver<Buffer> receiver) {
        if (receiver == null) {
            throw new NullPointerException("receiver cannot be null.");
        }

        this.receiver = receiver;
    }

    public void close () {
        //close game server connections
        if (this.gsConn != null) {
            this.gsConn.close();
        }
    }

    /**
    * handle special messages from client which arent redirected to game server, e.q. login messages
    */
    protected void handleProxyMsg (Buffer content) {
        if (content == null) {
            throw new NullPointerException("buffer cannot be null.");
        }

        if (content.length() < Config.MSG_HEADER_LENGTH) {
            throw new IllegalArgumentException("buffer doesnt contains full header.");
        }

        byte type = content.getByte(0);
        byte extendedType = content.getByte(1);
        short protocolVersion = content.getShort(2);

        //check, if message is RTT message
        if (type == Config.MSG_TYPE_PROXY) {
            if (extendedType == Config.MSG_EXTENDED_TYPE_RTT) {
                //MMOLogger.info("ClientConnection", "RTT message received");

                //send RTT response to client
                Buffer msg = MessageUtils.createRTTResponse();
                this.sendToClient(msg);

                return;
            } else if (extendedType == Config.MSG_EXTENDED_TYPE_PUBLIC_KEY_REQUEST) {
                MMOLogger.info("ClientConnection", "received RSA public key request.");

                //get key pair
                KeyPair keyPair = this.manager.getKeyPair();
                PublicKey publicKey = keyPair.getPublic();

                //send RTT response to client
                Buffer msg = MessageUtils.createPublicKeyResponse(publicKey);
                this.sendToClient(msg);

                return;
            }

            MMOLogger.info("ClientConnection", "no handler for special proxy message: 0x" + ByteUtils.byteToHex(content.getByte(0)));
        }

        //get handler
        MessageHandler<Buffer> handler = this.manager.getProxyHandler(type, extendedType, protocolVersion);

        if (handler == null) {
            MMOLogger.warn("ClientConnection", "no proxy handler specified for type 0x" + ByteUtils.byteToHex(content.getByte(0)));
            return;
        }

        MMOLogger.info("ClientConnection", "handle special proxy message: 0x" + ByteUtils.byteToHex(content.getByte(0)));
        handler.handle(content, type, extendedType, this, this.state);
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
        return this.state.isLoggedIn();
    }

    public int countBackendConnections () {
        return 0;
    }

    public void sendToClient (Buffer content) {
        this.receiver.receive(content);
    }

    public String getIP () {
        return this.ip;
    }

}
