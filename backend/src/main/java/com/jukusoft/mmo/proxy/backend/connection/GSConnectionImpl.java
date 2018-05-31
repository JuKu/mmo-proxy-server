package com.jukusoft.mmo.proxy.backend.connection;

import com.jukusoft.mmo.proxy.core.logger.MMOLogger;
import com.jukusoft.mmo.proxy.core.message.MessageReceiver;
import com.jukusoft.mmo.proxy.core.service.connection.GSConnection;
import com.jukusoft.mmo.proxy.core.stream.BufferStream;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

public class GSConnectionImpl implements GSConnection {

    protected NetClient client = null;
    protected BufferStream bufferStream = null;

    protected boolean isOpened = true;
    protected MessageReceiver<Buffer> receiver = null;

    protected Handler<Void> closeHandler = null;

    protected final String ip;
    protected final int port;

    public GSConnectionImpl (NetClient client, BufferStream bufferStream, NetSocket socket) {
        this.client = client;
        this.bufferStream = bufferStream;

        this.ip = socket.remoteAddress().host();
        this.port = socket.remoteAddress().port();
    }

    public void init () {
        //add handler
        this.bufferStream.handler(content -> {
            if (this.receiver != null) {
                this.receiver.receive(content);
            } else {
                MMOLogger.warn("gs handler", "Drop received message, because no message was receiver set.");
            }
        });

        //add exception handler
        this.bufferStream.exceptionHandler(e -> {
            MMOLogger.warn("GSConnectionImpl", "exception handler, ip: " + ip + ", port: " + port, e);
            e.printStackTrace();
        });

        this.bufferStream.endHandler(v -> {
            MMOLogger.warn("gs handler", "Close connection: ip: " + ip + ", port: " + port);

            if (closeHandler != null) {
                closeHandler.handle(v);
            }
        });
    }

    @Override
    public void send(Buffer content) {
        this.bufferStream.write(content);
    }

    @Override
    public void setReceiver(MessageReceiver<Buffer> receiver) {
        this.receiver = receiver;
    }

    @Override
    public void setCloseHandler(Handler<Void> handler) {
        this.closeHandler = handler;
    }

    @Override
    public boolean isOpened() {
        return this.isOpened;
    }

    @Override
    public void close() {
        this.client.close();
        this.client = null;
    }

}
