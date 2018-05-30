package com.jukusoft.mmo.proxy.database.character;

import com.jukusoft.mmo.proxy.core.character.CharacterSlot;
import com.jukusoft.mmo.proxy.core.character.ICharacterService;
import com.jukusoft.mmo.proxy.core.logger.MMOLogger;
import com.jukusoft.mmo.proxy.database.Database;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CharacterService implements ICharacterService {

    protected static final String SELECT_MY_CHARACTERS = "SELECT * FROM `{prefix}characters` WHERE `userID` = ?; ";
    protected static final String SELECT_CHARACTER_NAMES = "SELECT * FROM `{prefix}characters` WHERE `name` = ?; ";
    protected static final String INSERT_CHARACTER = "INSERT INTO `{prefix}characters` (" +
            "   `cid`, `name`, `type`, `userID`, `data`, `current_regionID`, `first_game`, `pos_x`, `pos_y`, `pos_z`, `auto_join`, `visible`" +
            ") VALUES (" +
            "   NULL, ?, 'PLAYER', ?, ?, ?, '1', '-1', '-1', '-1', '0', '1'" +
            "); ";

    public enum CREATE_CHARACTER_RESULT_CODES {

        /**
        * character was successfully created
        */
        SUCCESS(1),

        /**
        * character name already exists in database
        */
        NAME_ALREADY_EXISTS(2),

        /**
        * character name is invalide
        */
        INVALIDE_NAME(3);

        private final int resultCode;

        CREATE_CHARACTER_RESULT_CODES (int resultCode) {
            this.resultCode = resultCode;
        }

        public int getValue() {
            return this.resultCode;
        }

    }

    protected Pattern usernameValidatorPattern = Pattern.compile("[A-Za-z0-9_]+");

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

    @Override
    public void createCharacter(CharacterSlot character, int userID, Handler<Integer> handler) {
        //first, check if character name already exists
        String name = character.getName();

        //check, if name is valide
        if (!this.usernameValidatorPattern.matcher(name).matches()) {
            //character name is not valide
            handler.handle(CREATE_CHARACTER_RESULT_CODES.INVALIDE_NAME.getValue());

            return;
        }

        //check, if character name already exists
        if (this.existsCharacterName(name)) {
            //character name already exists
            handler.handle(CREATE_CHARACTER_RESULT_CODES.NAME_ALREADY_EXISTS.getValue());

            return;
        }

        //create character
        this.create(character, userID);

        handler.handle(CREATE_CHARACTER_RESULT_CODES.SUCCESS.getValue());
    }

    protected boolean existsCharacterName (String name) {
        try (Connection conn = Database.getConnection()) {
            //select
            PreparedStatement stmt = conn.prepareStatement(Database.replacePrefix(SELECT_CHARACTER_NAMES));
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            //return if select statement has one or more results (rows)
            return rs.next();
        } catch (SQLException e) {
            MMOLogger.warn("CharacterService", "SQLException while trying to check if character name already exists.", e);
            return true;
        }
    }

    protected void create (CharacterSlot character, int userID) {
        //TODO: search for start region for this race

        //TODO: create character here
    }

}
