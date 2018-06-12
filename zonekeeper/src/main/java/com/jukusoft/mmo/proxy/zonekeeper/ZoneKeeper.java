package com.jukusoft.mmo.proxy.zonekeeper;

import com.hazelcast.core.HazelcastInstance;
import com.jukusoft.mmo.proxy.core.logger.MMOLogger;
import com.jukusoft.mmo.proxy.core.utils.ByteUtils;
import io.vertx.core.eventbus.EventBus;

public class ZoneKeeper {

    protected final EventBus eventBus;
    protected final HazelcastInstance hazelcast;

    public ZoneKeeper (EventBus eventBus, HazelcastInstance hazelcast) {
        this.eventBus = eventBus;
        this.hazelcast = hazelcast;
    }

    public void start () {
        this.eventBus.consumer("get-server-by-region", handler -> {
            long id = (long) handler.body();

            int regionID = ByteUtils.getFirstIntegerFromLong(id);
            int instanceID = ByteUtils.getSecondIntegerFromLong(id);

            MMOLogger.info("ZoneKeeper", "requested zone for region " + regionID + ", instanceID: " + instanceID);
        });
    }

}
