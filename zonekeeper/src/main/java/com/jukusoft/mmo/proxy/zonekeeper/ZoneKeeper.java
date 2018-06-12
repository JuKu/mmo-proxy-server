package com.jukusoft.mmo.proxy.zonekeeper;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.jukusoft.mmo.proxy.core.logger.MMOLogger;
import com.jukusoft.mmo.proxy.core.utils.ByteUtils;
import com.jukusoft.mmo.proxy.core.utils.RandomUtils;
import io.vertx.core.eventbus.EventBus;

public class ZoneKeeper {

    protected final EventBus eventBus;
    protected final HazelcastInstance hazelcast;

    protected final IMap<Long,String> regionToServerMap;
    protected final IList<String> gsList;

    public ZoneKeeper (EventBus eventBus, HazelcastInstance hazelcast) {
        this.eventBus = eventBus;
        this.hazelcast = hazelcast;

        this.regionToServerMap = hazelcast.getMap("region-to-server");
        this.gsList = hazelcast.getList("gs-list");//list with all available game servers
    }

    public void start () {
        this.eventBus.consumer("get-server-by-region", handler -> {
            long id = (long) handler.body();

            int regionID = ByteUtils.getFirstIntegerFromLong(id);
            int instanceID = ByteUtils.getSecondIntegerFromLong(id);

            MMOLogger.info("ZoneKeeper", "ZoneKeeper: requested zone for region " + regionID + ", instanceID: " + instanceID);

            //look, if one of the game server already holds this region
            if (this.regionToServerMap.containsKey(id)) {
                //region is available on one of the game servers

                String server = this.regionToServerMap.get(id);
                handler.reply(server);

                MMOLogger.info("ZoneKeeper", "ZoneKeeper: region " + regionID + " is available on gs " + server);
            } else {
                //region isnt running yet

                if (this.gsList.isEmpty()) {
                    //no gs is available
                    handler.reply(null);

                    MMOLogger.warn("ZoneKeeper", "ZoneKeeper: no game server is available!");

                    return;
                }

                //choose one of this servers
                int i = RandomUtils.getRandomNumber(0, this.gsList.size());
                String server = this.gsList.get(i);

                //inform gs to load region
                this.eventBus.send("gs-" + server, id);

                MMOLogger.info("ZoneKeeper", "ZoneKeeper: region " + regionID + " was started now on gs " + server);

                handler.reply(server);
            }
        });
    }

}
