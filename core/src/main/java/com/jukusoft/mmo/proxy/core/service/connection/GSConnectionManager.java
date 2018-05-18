package com.jukusoft.mmo.proxy.core.service.connection;

import com.jukusoft.mmo.proxy.core.service.IService;

public interface GSConnectionManager extends IService {

    public GSConnection createConnection (final int sectorID);

}
