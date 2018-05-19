package com.jukusoft.mmo.proxy.backend;

import com.jukusoft.mmo.proxy.core.config.Config;
import com.jukusoft.mmo.proxy.core.logger.MMOLogger;
import com.jukusoft.mmo.proxy.core.service.connection.GSConnection;
import com.jukusoft.mmo.proxy.core.service.connection.GSConnectionManager;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

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

            //TODO: add code here
        });
    }

}
