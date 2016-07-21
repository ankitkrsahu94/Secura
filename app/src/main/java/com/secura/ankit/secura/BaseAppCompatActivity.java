package com.secura.ankit.secura;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.secura.ankit.secura.utils.Session;

/**
 * Created by ankit on 21/7/16.
 */
public class BaseAppCompatActivity extends AppCompatActivity {

    public  static boolean appInForeground = true;
    public static final long DISCONNECT_TIMEOUT = 10000;

    private Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {
        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
            Session.destroySession();
            checkSessionAlive();
            // Perform any required operation on disconnect
        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Toast.makeText(getApplicationContext(), String.valueOf(appInForeground), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Toast.makeText(getApplicationContext(), String.valueOf(appInForeground), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(getApplicationContext(), String.valueOf(appInForeground), Toast.LENGTH_SHORT).show();
        appInForeground = true;
        checkSessionAlive();
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        appInForeground = false;
        resetDisconnectTimer();
//        stopDisconnectTimer();
    }

    public static void checkSessionAlive(){
        //Log.e("Session is : ", String.valueOf(Session.sessionExists()) + ", App in Foreground : " + String.valueOf(appInForeground));
        if(!Session.sessionExists() && appInForeground){
            Log.e("Redirecting to login", "");
            Session.RedirectToLogin();
        }
    }
}


