package com.jukusoft.mmo.proxy.backend;

import com.jukusoft.mmo.proxy.backend.connection.GSConnectionImpl;
import com.jukusoft.mmo.proxy.core.config.Config;
import com.jukusoft.mmo.proxy.core.logger.MMOLogger;
import com.jukusoft.mmo.proxy.core.service.connection.GSConnection;
import com.jukusoft.mmo.proxy.core.service.connection.GSConnectionManager;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

public class GSConnectionManagerImpl implements GSConnectionManager {

    //vertx.io
    protected Vertx vertx = null;
    protected EventBus eventBus = null;

    public GSConnectionManagerImpl(Vertx vertx) {
        this.vertx = vertx;
        this.eventBus = vertx.eventBus();
    }

    @Override
    public void createConnection(int sectorID, Handler<GSConnection> handler) {
        if (sectorID < 1) {
            throw new IllegalArgumentException("sectorID has to be greater than 0.");
        }

        //TODO: find server for this sector
        this.eventBus.send("get-server-by-sector", sectorID, Config.EVENTBUS_DELIVERY_OPTIONS, res -> {
            if (!res.succeeded()) {
                //couldnt find zonekeeper instance
                MMOLogger.warn("GSConnectionManagerImpl", "Couldnt find sector zonekeeper, timeout reached (" + Config.EVENTBUS_DELIVERY_OPTIONS.getSendTimeout() + "ms). requested sector: " + sectorID + "");

                handler.handle(null);

                return;
            }

            //get ip and port
            String str = (String) res.result().body();
            String[] array = str.split(":");
            String ip = array[0];
            int port = Integer.parseInt(array[1]);

            MMOLogger.info("GSConnectionManagerImpl", "try to open game server connection, ip: " + ip + ", port: " + port);

            //open connection
            createConnection(ip, port, handler);
        });
    }

    protected void createConnection (String ip, int port, Handler<GSConnection> handler) {
        //set tcp client options
        NetClientOptions options = new NetClientOptions();
        options.setConnectTimeout(Config.TCP_CLIENT_CONNECTION_TIMEOUT);
        options.setReconnectAttempts(Config.TCP_RECONNECT_ATTEMPTS);
        options.setReconnectInterval(Config.TCP_RECONNECT_INTERVAL);

        //create tcp client
        final NetClient client = vertx.createNetClient(options);

        //connect to game server
        client.connect(port, ip, res -> {
            if (res.succeeded()) {
                MMOLogger.info("GSConnectionManagerImpl", "gs connection established, ip: " + ip + ", port: " + port + ".");

                NetSocket socket = res.result();

                //create new gs connection
                GSConnectionImpl conn = new GSConnectionImpl(client, socket);

                //initialize client
                conn.init();

                handler.handle(conn);
            } else {
                MMOLogger.info("GSConnectionManagerImpl", "Couldnt connect to game server, ip: " + ip + ", port: " + port + ", error: " + res.cause().getMessage() + ".");
                handler.handle(null);
            }
        });
    }

}
