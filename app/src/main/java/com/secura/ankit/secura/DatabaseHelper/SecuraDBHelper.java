package com.secura.ankit.secura.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.secura.ankit.secura.DatabaseSchema.SecuraContract;
import com.secura.ankit.secura.Session;
import com.secura.ankit.secura.utils.AESHelper;

import org.json.JSONObject;

import java.security.AccessControlContext;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

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

    public boolean createUser(String email, String password){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(SecuraContract.UserTable.EMAIL_ID, email);
            contentValues.put(SecuraContract.UserTable.PASSWORD, password);
            db.insert(SecuraContract.UserTable.TABLE_NAME, null, contentValues);
            db.close();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public int getUser(String email, String password){
        //System.out.println("1. here");
        int userID = -1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select user_id from " + SecuraContract.UserTable.TABLE_NAME +
                " where " + SecuraContract.UserTable.EMAIL_ID + " = \"" + email + "\" and " +
                SecuraContract.UserTable.PASSWORD + "= \"" + password + "\"", null);
        //System.out.println("2. here");
        if(res.getCount() > 0){
            res.moveToFirst();
            userID = res.getInt(res.getColumnIndex(SecuraContract.UserGroupMap.USER_ID));
        }
        //System.out.println("3. here");
        //db.close();
        return userID;
    }

    public void createGroupMap(String groupName, String email){
        int userID = getUser(email, Session.getSessionKey());
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ContentValues contentValues = new ContentValues();
        Date date = new Date();
/*
        if(userID == -1){
            createUser(email,);
            userID = getUser(email);
        }*/

        contentValues.put(SecuraContract.UserGroupMap.GROUP_NAME, groupName);
        contentValues.put(SecuraContract.UserGroupMap.DATE_ADDED, dateFormat.format(date));
        contentValues.put(SecuraContract.UserGroupMap.USER_ID, userID);
        db.insert(SecuraContract.UserGroupMap.TABLE_NAME, null, contentValues);
        db.close();
    }

    public int getGroupMapID(String groupName, String email){
        int mapID = -1;
        int userID = getUser(email, Session.getSessionKey());

        SQLiteDatabase db = this.getReadableDatabase();
        if(userID > -1){
            Cursor res = db.rawQuery("select " + SecuraContract.UserGroupMap.GROUP_ID + " from " +
                    SecuraContract.UserGroupMap.TABLE_NAME + " where " +
                    SecuraContract.UserGroupMap.GROUP_NAME + " = \"" + groupName + "\"" +
                    "and " +
                    SecuraContract.UserGroupMap.USER_ID + " = " + userID, null);

            res.moveToFirst();
            mapID = res.getInt(res.getColumnIndex(SecuraContract.UserGroupMap.GROUP_ID));
        }
        db.close();
        return mapID;
    }


    public int insertItem(int groupID, String itemName, String jsonData) throws Exception {
        //int mapID;
        /*insertUser(email, "password");
        getUser(email);
        createGroupMap("TestGroup 1", email);
        createGroupMap("TestGroup 2", email);
        mapID = getGroupMapID("TestGroup 1", "testuser@xyz.com");*/
        //AESHelper aesHelper = new AESHelper();
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        ContentValues contentValues = new ContentValues();
        contentValues.put(SecuraContract.UserGroupItemMap.ITEM_TITLE, itemName);
        contentValues.put(SecuraContract.UserGroupItemMap.DATE_ADDED, dateFormat.format(date));
        contentValues.put(SecuraContract.UserGroupItemMap.DATE_MODIFIED, dateFormat.format(date));
        contentValues.put(SecuraContract.UserGroupItemMap.DATA, AESHelper.encrypt(jsonData));
        contentValues.put(SecuraContract.UserGroupItemMap.MAP_ID, groupID);

        db.insert(SecuraContract.UserGroupItemMap.TABLE_NAME, null, contentValues);
        db.close();

        return 1;
    }

    public boolean checkExistingAccount(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("Select * from " + SecuraContract.UserTable.TABLE_NAME + " where 1", null);
        if(res.getCount() > 0){
            return true;
        }
        db.close();
        return  false;
    }

    public String getItemInfo(int item_id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + SecuraContract.UserGroupItemMap.TABLE_NAME +
                " where " + SecuraContract.UserGroupItemMap.ITEM_ID + " = " + item_id, null);
        res.moveToFirst();

        if(res.getCount() > 0){
//            System.out.println("Fuck data : " + res.getString(res.getColumnIndex(SecuraContract.UserGroupItemMap.DATA)));
            return res.getString(res.getColumnIndex(SecuraContract.UserGroupItemMap.DATA));
        }
        db.close();
        return "-1";
    }

    public LinkedHashMap<Integer, String> getItems(int map_id){
        SQLiteDatabase db = this.getReadableDatabase();
        LinkedHashMap<Integer, String> list = new LinkedHashMap<Integer, String>();
        int key;
        String val;
        Cursor res = db.rawQuery("select * from " + SecuraContract.UserGroupItemMap.TABLE_NAME +
                " where " + SecuraContract.UserGroupItemMap.MAP_ID + " = " + map_id, null);

        res.moveToFirst();
        while(!res.isAfterLast()){
            key = res.getInt(res.getColumnIndex(SecuraContract.UserGroupItemMap.ITEM_ID));
            val = res.getString(res.getColumnIndex(SecuraContract.UserGroupItemMap.ITEM_TITLE));
            list.put(key, val);
            res.moveToNext();
        }

        db.close();
        return list;
    }

    /*public ArrayList<String> getGroups(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        int userID = getUser(email);

        if(userID > -1){
            Cursor res = db.rawQuery("select * from " + SecuraContract.UserGroupMap.TABLE_NAME + " where user_id = " + userID, null);
            res.moveToFirst();
            while(!res.isAfterLast()){
                list.add(res.getString(res.getColumnIndex(SecuraContract.UserGroupMap.GROUP_NAME)));
                res.moveToNext();
            }
        }
        else{
            return new ArrayList<>();
        }

        db.close();
        return list;
    }*/

    public LinkedHashMap<Integer, String> getGroups(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        LinkedHashMap<Integer, String> list = new LinkedHashMap<>();
        int userID = 0;
        userID = getUser(email, Session.getSessionKey());

        int key;
        String val;

        if(userID > -1){
            Cursor res = db.rawQuery("select * from " + SecuraContract.UserGroupMap.TABLE_NAME + " where user_id = " + userID
                    + "", null);
            res.moveToFirst();
            while(!res.isAfterLast()){
                key = res.getInt(res.getColumnIndex(SecuraContract.UserGroupMap.GROUP_ID));
                val = res.getString(res.getColumnIndex(SecuraContract.UserGroupMap.GROUP_NAME));
                list.put(key, val);
                //Log.e("ID : ", key + "");
                res.moveToNext();
            }
        }
        else{
            return new LinkedHashMap<>();
        }

        db.close();
        return list;
    }
}
