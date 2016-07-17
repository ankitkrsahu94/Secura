package com.secura.ankit.secura;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.app.Activity;

/**
 * Created by Vassar-Dell-4 on 17-Jul-16.
 */
public class Session {
    // Shared Preferences
    static SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    static Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "SecuraUserPref";

    public static final String KEY_NAME = "sessionKey";

    // Constructor
    public Session(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void setSessionKey(String key){

        // Storing name in pref
        editor.putString(KEY_NAME, key);
        // commit changes
        editor.commit();
    }

    public static boolean sessionExists(){
        try{
            if(!pref.getString(KEY_NAME,"").equals("")){
                return true;
            }
        }catch (Exception e){
            Intent intent = new Intent(_context, Login.class);
            _context.startActivity(intent);
            //e.printStackTrace();
        }
        return false;
    }

    public static String getSessionKey(){
        if(sessionExists()){
            return pref.getString(KEY_NAME,"");
        }
        else{
            return "session-error";
        }
    }
    public static void destroySession(){
        pref.edit().clear().commit();
    }
}
