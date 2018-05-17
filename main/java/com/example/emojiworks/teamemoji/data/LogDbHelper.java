package com.example.emojiworks.teamemoji.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.emojiworks.teamemoji.data.LogDbContract.LogDbEntry;
/**
 * Created by ralphinator on 12/13/2016.
 */

public class  LogDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = LogDbHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME= "Inventory.db";
    public LogDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate (SQLiteDatabase db){
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + LogDbEntry.TABLE_NAME + " ("
                +                 LogDbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                +                 LogDbEntry.COLUMN_INVENTORY_NAME + " TEXT NOT NULL, "
                +                 LogDbEntry.COLUMN_INVENTORY_ACTIVITY + " TEXT, "
                +                 LogDbEntry.COLUMN_INVENTORY_WHERE + " TEXT UNIQUE, "
                +                 LogDbEntry.COLUMN_INVENTORY_WHOM + " TEXT, "
                +                 LogDbEntry.COLUMN_INVENTORY_DESCRIPTION + " TEXT, "
                +                 LogDbEntry.COLUMN_INVENTORY_TIME + " INTEGER NOT NULL DEFAULT 0, "
                +                 LogDbEntry.COLUMN_INVENTORY_INTERVAL + " INTEGER NOT NULL DEFAULT 0);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("TABLE IF EXISTS " +LogDbEntry.TABLE_NAME);
        onCreate(db);
    }
}