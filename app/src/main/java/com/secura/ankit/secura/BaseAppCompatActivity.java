package com.secura.ankit.secura;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.secura.ankit.secura.utils.Session;

/**
 * Created by ankit on 21/7/16.
 */
public class BaseAppCompatActivity extends AppCompatActivity {

    public  static boolean appInForeground = true;
    public static final long DISCONNECT_TIMEOUT = 10000;
    ProgressDialog pd;

    private Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {
        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
//            Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
            // Session.destroySession();
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
        checkSessionAlive();
        //Toast.makeText(getApplicationContext(), String.valueOf(appInForeground), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkSessionAlive();
        //Toast.makeText(getApplicationContext(), String.valueOf(appInForeground), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(getApplicationContext(), String.valueOf(appInForeground), Toast.LENGTH_SHORT).show();
        appInForeground = true;
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        appInForeground = false;
        resetDisconnectTimer();
//        stopDisconnectTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pd.dismiss();
    }

    public void checkSessionAlive(){
        //Log.e("Session is : ", String.valueOf(Session.sessionExists()) + ", App in Foreground : " + String.valueOf(appInForeground));
        if(!Session.sessionExists()){
            System.out.println("Session Exists : " + Session.sessionExists());
            //Log.e("Redirecting to login", "");
            pd.cancel();
            try{
                ((Activity)getApplicationContext()).finish();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            Session.destroySession();
            finish();
        }
        else{
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


