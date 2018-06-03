package com.jukusoft.mmo.proxy.core.character;

import com.jukusoft.mmo.proxy.core.region.RegionMetaData;
import com.jukusoft.mmo.proxy.core.service.IService;
import io.vertx.core.Handler;

import java.util.List;

public interface ICharacterService extends IService {

    public List<CharacterSlot> listSlotsOfUser (int userID);

    public void createCharacter (CharacterSlot character, int userID, Handler<Integer> handler);

    public boolean checkCIDBelongsToPlayer (int cid, int userID);

    public void getCurrentRegionOfCharacter (int cid, Handler<RegionMetaData> handler);

}
