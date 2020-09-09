package com.pachain.android.tool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.pachain.android.config.Config;
import com.pachain.android.sqlite.MySQLiteOpenHelper;

public class DBManager {
    private static MySQLiteOpenHelper helper;
    private static SQLiteDatabase db;
    private static int mCount;
    private static DBManager mMnanagerInstance;

    private DBManager(Context context) {
        helper = new MySQLiteOpenHelper(context, Config.DB_VERSION);
    }

    public static synchronized DBManager getIntance(Context context){
        if(mMnanagerInstance == null){
            return new DBManager(context);
        }
        return mMnanagerInstance;
    }

    public synchronized SQLiteDatabase openDb(){
        if(mCount == 0){
            db = helper.getWritableDatabase();
        }
        mCount++;
        return db;
    }

    public synchronized void closeDb(SQLiteDatabase database){
        mCount--;
        if(mCount == 0){
            database.close();
        }
    }
}
