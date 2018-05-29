package com.jukusoft.mmo.proxy.core.character;

import com.jukusoft.mmo.proxy.core.service.IService;

import java.util.List;

public interface ICharacterService extends IService {

    public List<CharacterSlot> listSlotsOfUser (int userID);

}
