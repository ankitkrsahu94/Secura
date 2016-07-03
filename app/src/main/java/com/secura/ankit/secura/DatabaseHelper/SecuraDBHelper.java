package com.secura.ankit.secura.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.secura.ankit.secura.DatabaseSchema.SecuraContract;

import java.security.AccessControlContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Vassar-Dell-4 on 03-Jul-16.
 */
public class SecuraDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Secura.db";

    public static String email = "testuser@xyz.com";

    public SecuraDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SecuraContract.UserTable.CREATE_USER_TABLE);
        //db.execSQL(SecuraContract.Group.CREATE_ITEMGROUP);
        db.execSQL(SecuraContract.UserGroupMap.CREATE_USERGROUPMAP);
        db.execSQL(SecuraContract.UserGroupItemMap.CREATE_ITEMS_TABLE);
        db.execSQL(SecuraContract.ActivityLog.CREATE_LOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertUser(String email, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SecuraContract.UserTable.EMAIL_ID, email);
        contentValues.put(SecuraContract.UserTable.PASSWORD, password);
        db.insert(SecuraContract.UserTable.TABLE_NAME, null, contentValues);
        db.close();
    }

    public int getUser(String email){
        int userID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select user_id from " + SecuraContract.UserTable.TABLE_NAME + " where email_id = \"" + email + "\"", null);
        res.moveToFirst();
        userID = res.getInt(res.getColumnIndex(SecuraContract.UserGroupMap.USER_ID));
        //db.close();
        return userID;
    }

    public void createGroupMap(String groupName, String email){
        int userID = getUser(email);
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ContentValues contentValues = new ContentValues();
        Date date = new Date();

        contentValues.put(SecuraContract.UserGroupMap.GROUP_NAME, groupName);
        contentValues.put(SecuraContract.UserGroupMap.DATE_ADDED, dateFormat.format(date));
        contentValues.put(SecuraContract.UserGroupMap.USER_ID, userID);
        db.insert(SecuraContract.UserGroupMap.TABLE_NAME, null, contentValues);
        db.close();
    }

    public int getGroupMapID(String groupName, String email){
        int mapID=1;
        int userID = getUser(email);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + SecuraContract.UserGroupMap.GROUP_ID + " from " +
                SecuraContract.UserGroupMap.TABLE_NAME + " where " +
                SecuraContract.UserGroupMap.GROUP_NAME + " = \"" + groupName + "\"" +
                "and " +
                SecuraContract.UserGroupMap.USER_ID + " = " + userID, null);

        res.moveToFirst();
        mapID = res.getInt(res.getColumnIndex(SecuraContract.UserGroupMap.GROUP_ID));
        db.close();
        return mapID;
    }


    public int insertItem(String itemName){
        int mapID;
        insertUser(email, "password");
        getUser(email);
        createGroupMap("TestGroup 1", email);
        createGroupMap("TestGroup 2", email);
        mapID = getGroupMapID("TestGroup 1", "testuser@xyz.com");
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        ContentValues contentValues = new ContentValues();
        contentValues.put(SecuraContract.UserGroupItemMap.ITEM_TITLE, itemName);
        contentValues.put(SecuraContract.UserGroupItemMap.DATE_ADDED, dateFormat.format(date));
        contentValues.put(SecuraContract.UserGroupItemMap.DATE_MODIFIED, dateFormat.format(date));
        contentValues.put(SecuraContract.UserGroupItemMap.DATA, "{}");
        contentValues.put(SecuraContract.UserGroupItemMap.MAP_ID, mapID);

        db.insert(SecuraContract.UserGroupItemMap.TABLE_NAME, null, contentValues);
        db.close();
        return 1;
    }

    public ArrayList<String> getItems(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        Cursor res = db.rawQuery("select * from " + SecuraContract.UserGroupItemMap.TABLE_NAME, null);

        res.moveToFirst();
        while(!res.isAfterLast()){
            list.add(res.getString(res.getColumnIndex(SecuraContract.UserGroupItemMap.ITEM_TITLE)));
            res.moveToNext();
        }

        db.close();
        return list;
    }

    public ArrayList<String> getGroups(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        int userID = getUser(email);
        Cursor res = db.rawQuery("select * from " + SecuraContract.UserGroupMap.TABLE_NAME + " where user_id = " + userID, null);

        res.moveToFirst();
        while(!res.isAfterLast()){
            list.add(res.getString(res.getColumnIndex(SecuraContract.UserGroupMap.GROUP_NAME)));
            res.moveToNext();
        }

        db.close();
        return list;
    }
}
