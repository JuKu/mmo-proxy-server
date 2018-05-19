package com.jukusoft.mmo.proxy.backend.connection;

import com.jukusoft.mmo.proxy.core.logger.MMOLogger;
import com.jukusoft.mmo.proxy.core.message.MessageReceiver;
import com.jukusoft.mmo.proxy.core.service.connection.GSConnection;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

public class GSConnectionImpl implements GSConnection {

    protected NetClient client = null;
    protected NetSocket socket = null;

    protected boolean isOpened = true;
    protected MessageReceiver<Buffer> receiver = null;

    protected Handler<Void> closeHandler = null;

    public GSConnectionImpl (NetClient client, NetSocket socket) {
        this.client = client;
        this.socket = socket;
    }

    public void init () {
        //add handler
        this.socket.handler(content -> {
            if (this.receiver != null) {
                this.receiver.receive(content);
            } else {
                MMOLogger.warn("gs handler", "Drop received message, because no message was receiver set.");
            }
        });

        final String ip = this.socket.remoteAddress().host();
        final int port = this.socket.remoteAddress().port();

        //add exception handler
        this.socket.exceptionHandler(e -> {
            MMOLogger.warn("GSConnectionImpl", "exception handler, ip: " + ip + ", port: " + port, e);
            e.printStackTrace();
        });

        this.socket.closeHandler(v -> {
            MMOLogger.warn("gs handler", "Close connection: ip: " + ip + ", port: " + port);

            if (closeHandler != null) {
                closeHandler.handle(v);
            }
        });
    }

    @Override
    public void send(Buffer content) {
        this.socket.write(content);
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
