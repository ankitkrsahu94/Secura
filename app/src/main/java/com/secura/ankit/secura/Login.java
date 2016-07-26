package com.secura.ankit.secura;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.secura.ankit.secura.DatabaseHelper.SecuraDBHelper;
import com.secura.ankit.secura.utils.AESHelper;
import com.secura.ankit.secura.utils.Session;

import java.security.GeneralSecurityException;

/**
 * Created by ankit on 17-Jul-16.
 */
public class Login extends Activity {

    LinearLayout register, login;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        register = (LinearLayout) findViewById(R.id.register);
        login = (LinearLayout) findViewById(R.id.login);

        pd = new ProgressDialog(Login.this);
        pd.setMessage("Initializing ...");
        pd.show();
        new Initialize().execute();

        Button registerBtn = (Button) findViewById(R.id.registerBtn);
        Button loginBtn = (Button) findViewById(R.id.loginBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = ((EditText) findViewById(R.id.password)).getText().toString();
                String confirmPass = ((EditText) findViewById(R.id.confirmPassword)).getText().toString();

                if(password.equals("") || confirmPass.equals("")){
                    Toast.makeText(Login.this, "Please create a new password to create an account", Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(confirmPass)){
                    Toast.makeText(Login.this, "Passwords do not match", Toast.LENGTH_SHORT);
                }
                else{
                    pd.setMessage("Creating new Account.. Please Wait");
                    pd.show();
                    new Register().execute(password);
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = ((EditText)findViewById(R.id.loginPass)).getText().toString();
                pd.setMessage("Authenticating...");
                pd.show();
                new UserLogin().execute(password);
            }
        });

    }

    private class Initialize extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            SecuraDBHelper db = new SecuraDBHelper(getApplicationContext());

            boolean firstTime;
            firstTime = db.checkExistingAccount();
            return String.valueOf(firstTime);
        }

        @Override
        protected void onPostExecute(String result) {
            if(Boolean.valueOf(result)){
                login.setVisibility(View.VISIBLE);
            }
            else{
                register.setVisibility(View.VISIBLE);
            }
            pd.dismiss();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private class Register extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result, encPass;
            result = String.valueOf(false);
            SecuraDBHelper db = new SecuraDBHelper(Login.this);
            try {
                encPass = AESHelper.encrypt(params[0]);
                db.createUser(SecuraDBHelper.email, encPass);
                Session session = new Session(Login.this);
                session.setSessionKey(encPass);
                result = String.valueOf(true);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if(Boolean.valueOf(result)){
                Toast.makeText(Login.this, "Congratulations !!! Account has been created.", Toast.LENGTH_SHORT).show();
                //Toast.makeText(Login.this, "Your session key is + " + Session.getSessionKey(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Login.this, Dashboard.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(Login.this, "A new account cannot be created. Sorry for inconvenience caused.", Toast.LENGTH_SHORT).show();
            }
            pd.dismiss();
            finish();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private class UserLogin extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            SecuraDBHelper db = new SecuraDBHelper(Login.this);
            int userID = -1;
            //System.out.println("DOINBACK");
            try {
                String encPass = AESHelper.encrypt(params[0]);
                userID = db.getUser(SecuraDBHelper.email, encPass);
                Session session = new Session(Login.this);
                session.setSessionKey(encPass);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            return String.valueOf(userID);
        }

        @Override
        protected void onPostExecute(String result) {
            AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            if(Integer.parseInt(result) == -1){
                alertDialog.setMessage("Authentication Failed");
                alertDialog.show();
            }
            else{
                //alertDialog.setMessage("Authentication Successfull");
                ((EditText)findViewById(R.id.loginPass)).setText("");
                Intent intent = new Intent(Login.this, Dashboard.class);
                pd.dismiss();
                startActivity(intent);
                //finish();
            }
            pd.dismiss();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
