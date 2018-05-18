package com.jukusoft.mmo.proxy.core.service.connection;

import com.jukusoft.mmo.proxy.core.service.IService;
import io.vertx.core.Handler;

public interface GSConnectionManager extends IService {

    public void createConnection (final int sectorID, Handler<GSConnection> handler);

}
