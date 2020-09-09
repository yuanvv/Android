package com.pachain.android.common;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.pachain.android.entity.CountyEntity;
import com.pachain.android.entity.PartyEntity;
import com.pachain.android.entity.PrecinctEntity;
import com.pachain.android.entity.StateEntity;
import com.pachain.android.entity.VoterEntity;
import com.pachain.android.tool.DBManager;
import java.util.ArrayList;
import java.util.HashMap;

public class DataToolPackage {
    private DBManager dbManager;
    private SQLiteDatabase database;
    private Context context;

    public DataToolPackage(Context context, DBManager dbManager) {
        this.context = context;
        this.dbManager = dbManager;
    }

    public ArrayList<StateEntity> getStates() {
        ArrayList<StateEntity> states = new ArrayList<>();
        database = dbManager.openDb();
        Cursor cursor_states = database.rawQuery("SELECT * FROM States;", null);
        StateEntity state;
        while (cursor_states.moveToNext()) {
            state = new StateEntity();
            state.setID(cursor_states.getInt(cursor_states.getColumnIndex("ID")));
            state.setCode(cursor_states.getString(cursor_states.getColumnIndex("Code")));
            state.setName(cursor_states.getString(cursor_states.getColumnIndex("Name")));
            states.add(state);
        }
        cursor_states.close();
        dbManager.closeDb(database);
        return states;
    }

    public ArrayList<CountyEntity> getCounties() {
        ArrayList<CountyEntity> counties = new ArrayList<>();
        database = dbManager.openDb();
        Cursor cursor_counties = database.rawQuery("SELECT * FROM Counties ORDER BY ListOrder;", null);
        CountyEntity county;
        while (cursor_counties.moveToNext()) {
            county = new CountyEntity();
            county.setID(cursor_counties.getInt(cursor_counties.getColumnIndex("ID")));
            county.setCode(cursor_counties.getString(cursor_counties.getColumnIndex("Code")));
            county.setName(cursor_counties.getString(cursor_counties.getColumnIndex("Name")));
            county.setState(cursor_counties.getString(cursor_counties.getColumnIndex("State")));
            county.setNumber(cursor_counties.getString(cursor_counties.getColumnIndex("Number")));
            counties.add(county);
        }
        cursor_counties.close();
        dbManager.closeDb(database);
        return counties;
    }

    public HashMap<String, CountyEntity> getCountiesMap() {
        HashMap<String, CountyEntity> counties = new HashMap<>();
        database = dbManager.openDb();
        Cursor cursor_counties = database.rawQuery("SELECT * FROM Counties ORDER BY ListOrder;", null);
        CountyEntity county;
        while (cursor_counties.moveToNext()) {
            county = new CountyEntity();
            county.setID(cursor_counties.getInt(cursor_counties.getColumnIndex("ID")));
            county.setCode(cursor_counties.getString(cursor_counties.getColumnIndex("Code")));
            county.setName(cursor_counties.getString(cursor_counties.getColumnIndex("Name")));
            county.setState(cursor_counties.getString(cursor_counties.getColumnIndex("State")));
            county.setNumber(cursor_counties.getString(cursor_counties.getColumnIndex("Number")));
            counties.put(county.getNumber(), county);
        }
        cursor_counties.close();
        dbManager.closeDb(database);
        return counties;
    }

    public ArrayList<PrecinctEntity> getPrecincts(String state, String county) {
        ArrayList<PrecinctEntity> precincts = new ArrayList<>();
        database = dbManager.openDb();
        Cursor cursor = database.rawQuery("SELECT * FROM Precincts WHERE State='" + state + "' AND County='" + county.replace("'", "''") + "' ORDER BY ListOrder;", null);
        PrecinctEntity precinct;
        while (cursor.moveToNext()) {
            precinct = new PrecinctEntity();
            precinct.setState(cursor.getString(cursor.getColumnIndex("State")));
            precinct.setCounty(cursor.getString(cursor.getColumnIndex("County")));
            precinct.setNumber(cursor.getString(cursor.getColumnIndex("Number")));
            precinct.setName(cursor.getString(cursor.getColumnIndex("Name")));
            precincts.add(precinct);
        }
        cursor.close();
        dbManager.closeDb(database);
        return precincts;
    }

    public HashMap<String, PartyEntity> getParties() {
        HashMap<String, PartyEntity> parties = new HashMap<>();
        database = dbManager.openDb();
        Cursor cursor_parties = database.rawQuery("SELECT * FROM Parties", null);
        PartyEntity entity;
        while (cursor_parties.moveToNext()) {
            entity = new PartyEntity();
            entity.setID(cursor_parties.getInt(cursor_parties.getColumnIndex("ID")));
            entity.setCode(cursor_parties.getString(cursor_parties.getColumnIndex("Code")));
            entity.setName(cursor_parties.getString(cursor_parties.getColumnIndex("Name")));
            parties.put(entity.getName().toLowerCase(), entity);
        }
        cursor_parties.close();
        dbManager.closeDb(database);
        return parties;
    }

    public HashMap<String, String> getLocalVotes(String ballotNumber) {
        HashMap<String, String> votes = new HashMap<>();
        database = dbManager.openDb();
        Cursor cursor = database.rawQuery("SELECT * FROM VotingBallot WHERE BallotNumber='" + ballotNumber + "'", null);
        while (cursor.moveToNext()) {
            votes.put(cursor.getString(cursor.getColumnIndex("BallotNumber")).toLowerCase(), cursor.getString(cursor.getColumnIndex("Votes")));
        }
        cursor.close();
        dbManager.closeDb(database);
        return votes;
    }

    public boolean checkVerify(String ballotNumber) {
        boolean verified = false;
        database = dbManager.openDb();
        Cursor cursor = database.rawQuery("SELECT * FROM VerifyBallot WHERE BallotNumber='" + ballotNumber + "'", null);
        while (cursor.moveToNext()) {
            verified = true;
        }
        cursor.close();
        dbManager.closeDb(database);
        return verified;
    }

    public VoterEntity getRegisteredVoter() {
        VoterEntity voter = null;
        database = dbManager.openDb();
        Cursor cursor = database.rawQuery("SELECT * FROM Users LIMIT 1;", null);
        if (cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
                voter = new VoterEntity();
                voter.setVoterID(cursor.getLong(cursor.getColumnIndex("VoterID")));
                voter.setPublicKey(cursor.getString(cursor.getColumnIndex("PublicKey")));
                voter.setState(cursor.getString(cursor.getColumnIndex("State")));
                voter.setCounty(cursor.getString(cursor.getColumnIndex("County")));
                voter.setPrecinctNumber(cursor.getString(cursor.getColumnIndex("PrecinctNumber")));
                voter.setFirstName(cursor.getString(cursor.getColumnIndex("FirstName")));
                voter.setMiddleName(cursor.getString(cursor.getColumnIndex("MiddleName")));
                voter.setLastName(cursor.getString(cursor.getColumnIndex("LastName")));
                voter.setNameSuffix(cursor.getString(cursor.getColumnIndex("NameSuffix")));
                voter.setCellPhone(cursor.getString(cursor.getColumnIndex("CellPhone")));
                voter.setEmail(cursor.getString(cursor.getColumnIndex("Email")));
                voter.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
                voter.setSignature(cursor.getString(cursor.getColumnIndex("Signature")));
                voter.setCertificateType(cursor.getString(cursor.getColumnIndex("CertificateType")));
                voter.setCertificateFront(cursor.getString(cursor.getColumnIndex("CertificateFront")));
                voter.setCertificateBack(cursor.getString(cursor.getColumnIndex("CertificateBack")));
                voter.setFacePhoto(cursor.getString(cursor.getColumnIndex("FacePhoto")));
                voter.setEnableFingerprint(cursor.getInt(cursor.getColumnIndex("EnableFingerprint")) > 0);
                voter.setRegisteredDate(cursor.getString(cursor.getColumnIndex("RegisteredDate")));
                voter.setVerifiedDate(cursor.getString(cursor.getColumnIndex("VerifiedDate")));
                voter.setAccessToken(cursor.getString(cursor.getColumnIndex("AccessToken")));
            }
        }
        cursor.close();
        dbManager.closeDb(database);
        return voter;
    }

    public HashMap<String, String> getVotedKeys(String ballotNumber) {
        HashMap<String, String> keys = new HashMap<>();
        database = dbManager.openDb();
        Cursor cursor = database.rawQuery("SELECT * FROM VotingBallotOnions WHERE BallotNumber='" + ballotNumber.replace("'", "''") + "' AND PackageLevel=1", null);
        while (cursor.moveToNext()) {
            keys.put(cursor.getString(cursor.getColumnIndex("PublicKey")), cursor.getString(cursor.getColumnIndex("OnionKey")));
        }
        cursor.close();
        dbManager.closeDb(database);
        return keys;
    }

    public String getRegisterLink(String state, String county) {
        String link = "";
        database = dbManager.openDb();
        Cursor cursor = database.rawQuery("SELECT * FROM Links WHERE State='" + state + "' AND CountyNumber='" + county + "'", null);
        while (cursor.moveToNext()) {
            link = cursor.getString(cursor.getColumnIndex("RegisterLink"));
        }
        cursor.close();
        dbManager.closeDb(database);
        return link;
    }

    public String getOfficialsLink(String state, String county) {
        String link = "";
        database = dbManager.openDb();
        Cursor cursor = database.rawQuery("SELECT * FROM Links WHERE State='" + state + "' AND CountyNumber='" + county + "'", null);
        while (cursor.moveToNext()) {
            link = cursor.getString(cursor.getColumnIndex("Officials"));
        }
        cursor.close();
        dbManager.closeDb(database);
        return link;
    }

    public String getSOELink(String state, String county) {
        String link = "";
        database = dbManager.openDb();
        Cursor cursor = database.rawQuery("SELECT * FROM Links WHERE State='" + state + "' AND CountyNumber='" + county + "'", null);
        while (cursor.moveToNext()) {
            link = cursor.getString(cursor.getColumnIndex("SOELink"));
        }
        cursor.close();
        dbManager.closeDb(database);
        return link;
    }
}
