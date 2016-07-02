package com.secura.ankit.secura.DatabaseSchema;

import android.provider.BaseColumns;

/**
 * Created by Vassar-Dell-4 on 03-Jul-16.
 */
public class SecuraContract {
    public SecuraContract(){}

    public static abstract class UserTable implements BaseColumns{
        public static final String TABLE_NAME = "user";
        public static final String USER_ID = "user_id";
        public static final String EMAIL_ID = "email_id";
        public static final String PASSWORD = "password";
    }

    public static abstract class ItemType implements BaseColumns{
        public static final String TABLE_NAME = "items";
        public static final String ITEM_ID = "item_id";
        public static final String ITEM_TITLE = "title";
        public static final String DATA = "data";
        public static final String DATE_ADDED = "date_added";
        public static final String DATE_MODIFIED = "date_modified";
    }

    public static abstract class ActivityLog implements BaseColumns{
        public static final String ACTIVITY_ID = "activity_id";
        public static final String USER_ID = "user_id";
        public static final String OBJECT_ID = "object_id";
        public static final String ACTION = "action";
    }

}
