package com.pachain.android.data;

import android.database.sqlite.SQLiteDatabase;

public class Parties {
    public static void init(SQLiteDatabase database) {
        database.execSQL("DELETE FROM Parties");
        database.execSQL("INSERT INTO Parties(ID, Code, Name, ListOrder) SELECT 1, 'D', 'Democratic', 1 UNION ALL SELECT 2, 'R', 'Republican', 2 UNION ALL SELECT 3, 'I', 'Independent', 3 UNION ALL SELECT 4, 'O', 'Other', 4");
    }
}
