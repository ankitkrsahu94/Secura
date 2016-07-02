package com.secura.ankit.secura.DatabaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.secura.ankit.secura.DatabaseSchema.SecuraContract;

import java.security.AccessControlContext;

/**
 * Created by Vassar-Dell-4 on 03-Jul-16.
 */
public class SecuraDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Secura.db";

    public SecuraDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SecuraContract.UserTable.CREATE_USER_TABLE);
        db.execSQL(SecuraContract.ItemType.CREATE_ITEMS_TABLE);
        db.execSQL(SecuraContract.ActivityLog.CREATE_LOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
