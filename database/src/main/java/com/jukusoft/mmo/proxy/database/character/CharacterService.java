package com.jukusoft.mmo.proxy.database.character;

import com.jukusoft.mmo.proxy.core.character.CharacterSlot;
import com.jukusoft.mmo.proxy.core.character.ICharacterService;
import com.jukusoft.mmo.proxy.core.logger.MMOLogger;
import com.jukusoft.mmo.proxy.database.Database;
import io.vertx.core.json.JsonObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CharacterService implements ICharacterService {

    protected static final String SELECT_MY_CHARACTERS = "SELECT * FROM `{prefix}characters` WHERE `userID` = ?; ";

    public CharacterService () {
        //
    }

    @Override
    public List<CharacterSlot> listSlotsOfUser(int userID) {
        try (Connection conn = Database.getConnection()) {
            //select characters
            PreparedStatement stmt = conn.prepareStatement(Database.replacePrefix(SELECT_MY_CHARACTERS));
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            //create new empty list
            List<CharacterSlot> slots = new ArrayList<>();

            while (rs.next()) {
                int cid = rs.getInt("cid");
                String name = rs.getString("name");
                String type = rs.getString("type");
                String data = rs.getString("data");//json data
                int current_regionID = rs.getInt("current_regionID");

                //create character slot from json
                CharacterSlot slot = CharacterSlot.createFromJson(cid, name, new JsonObject(data));

                //add slot to list
                slots.add(slot);
            }

            return slots;
        } catch (SQLException e) {
            MMOLogger.warn("CharacterService", "SQLException while try to get character slots.", e);
            return new ArrayList<>();
        }
    }

}
