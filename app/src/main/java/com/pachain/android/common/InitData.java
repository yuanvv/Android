package com.pachain.android.common;

import android.database.sqlite.SQLiteDatabase;
import com.pachain.android.data.Counties;
import com.pachain.android.data.Links;
import com.pachain.android.data.Parties;
import com.pachain.android.data.Precincts_FL;
import com.pachain.android.data.Precincts_VA;
import com.pachain.android.data.States;

public class InitData {
    private SQLiteDatabase database;

    public InitData(SQLiteDatabase db) {
        this.database = db;
        Parties.init(database);
        States.init(database);
        Counties.init(database);
        Links.init(database);

        database.execSQL("DELETE FROM Precincts");
        Precincts_FL.init(database);
        Precincts_VA.init(database);
    }
}
