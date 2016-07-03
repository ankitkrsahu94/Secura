package com.secura.ankit.secura.DatabaseSchema;

import android.provider.BaseColumns;

/**
 * Created by Vassar-Dell-4 on 03-Jul-16.
 */
public class SecuraContract {
    public SecuraContract(){}

    public static abstract class UserTable implements BaseColumns{
        public static final String TABLE_NAME = "user";
        public static final String EMAIL_ID = "email_id";
        public static final String USER_ID = "user_id";
        public static final String PASSWORD = "password";

        public static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + USER_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT," + EMAIL_ID + " TEXT," + PASSWORD + " TEXT )";
    }

    /*public static abstract class Group implements BaseColumns{
        public static final String TABLE_NAME = "item_group";
        public static final String GROUP_NAME = "group_name";
        public static final String GROUP_ID = "group_id";
        public static final String DATE_ADDED = "date_added";

        public static final String CREATE_ITEMGROUP =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GROUP_NAME + " TEXT, " +
                DATE_ADDED + " TEXT)";
    }*/

    public static abstract class UserGroupMap implements BaseColumns{
        public static final String TABLE_NAME = "user_group_map";
        public static final String USER_ID = "user_id";
        public static final String GROUP_ID = "group_id";
        public static final String GROUP_NAME = "group_name";
        public static final String DATE_ADDED = "date_added";

        public static final String CREATE_USERGROUPMAP = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_ID + " INTEGER NOT NULL, " +
                GROUP_NAME + " TEXT, " +
                DATE_ADDED + " TEXT, " +
                "FOREIGN KEY(" + USER_ID + ") REFERENCES " + UserTable.TABLE_NAME + "(" + UserTable.USER_ID +")" +
                " )";
    }

    public static abstract class UserGroupItemMap implements BaseColumns{
        public static final String TABLE_NAME = "items";
        public static final String ITEM_ID = "item_id";
        public static final String ITEM_TITLE = "title";
        public static final String DATA = "data";
        public static final String DATE_ADDED = "date_added";
        public static final String DATE_MODIFIED = "date_modified";
        public static final String MAP_ID = "map_id";

        public static final String CREATE_ITEMS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + ITEM_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ITEM_TITLE + " TEXT," +
                DATA + " TEXT," +
                DATE_ADDED + " TEXT," +
                DATE_MODIFIED + " TEXT, " +
                MAP_ID + " INTEGER NOT NULL,  FOREIGN KEY(" + MAP_ID + ") REFERENCES " + UserGroupMap.TABLE_NAME + "(" + UserGroupMap.GROUP_ID + "))";
    }

    public static abstract class ActivityLog implements BaseColumns{
        public static final String TABLE_NAME = "activity_log";
        public static final String ACTIVITY_ID = "activity_id";
        public static final String USER_ID = "user_id";
        public static final String OBJECT_ID = "object_id";
        public static final String ACTION = "action";

        public static final String CREATE_LOG_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " + USER_ID + " INTEGER NOT NULL," +
                ACTIVITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + OBJECT_ID + " INTEGER NOT NULL," + ACTION + " TEXT, " +
                "FOREIGN KEY(" + OBJECT_ID + ") " + "REFERENCES " + UserGroupItemMap.TABLE_NAME + "(" + UserGroupItemMap.ITEM_ID + "), " +
                "FOREIGN KEY(" + USER_ID +" ) REFERENCES " + UserTable.TABLE_NAME + "(" + UserTable.USER_ID + "))";
    }
}
