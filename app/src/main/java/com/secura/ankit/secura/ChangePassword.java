package com.secura.ankit.secura;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.secura.ankit.secura.DatabaseHelper.SecuraDBHelper;
import com.secura.ankit.secura.utils.AESHelper;
import com.secura.ankit.secura.utils.Session;

import org.w3c.dom.Text;

import java.security.GeneralSecurityException;

/**
 * Created by Vassar-Dell-4 on 26-Jul-16.
 */
public class ChangePassword extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_changepassword);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button cancel = (Button) findViewById(R.id.cancel);
        Button change = (Button) findViewById(R.id.change);

        CheckBox cb = (CheckBox) findViewById(R.id.showPass);
        final EditText password = (EditText) findViewById(R.id.currentPassword);
        final EditText newPass = (EditText) findViewById(R.id.newPassword);
        final EditText confirmPass = (EditText) findViewById(R.id.confirmPassword);

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    password.setTransformationMethod(null);
                    newPass.setTransformationMethod(null);
                    confirmPass.setTransformationMethod(null);
                }
                else{
                    password.setTransformationMethod(new PasswordTransformationMethod());
                    newPass.setTransformationMethod(new PasswordTransformationMethod());
                    confirmPass.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        pd = new ProgressDialog(this);
    }

    public void handleUserResponse(View v){
        switch (v.getId()){
            case R.id.passwordChanged:
            case R.id.cancel:
                finish();
                break;
            case R.id.change:
                String password = ((EditText) findViewById(R.id.currentPassword)).getText().toString();
                String newPass = ((EditText) findViewById(R.id.newPassword)).getText().toString();
                String confirmPass = ((EditText) findViewById(R.id.confirmPassword)).getText().toString();
                if(newPass.equals(confirmPass)){
                    pd.setMessage("Authenticating User");
                    pd.show();
                    new AuthenticateUser().execute(password);
                }
                else{
                    AlertDialog alertDialog = new AlertDialog.Builder(ChangePassword.this).create();
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                    alertDialog.setMessage("Passwords do not match");
                    alertDialog.show();
                }
                break;
        }
    }

    private class AuthenticateUser extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            SecuraDBHelper db = new SecuraDBHelper(ChangePassword.this);
            int userID = -1;
            //System.out.println("DOINBACK");
            try {
                String encPass = AESHelper.encrypt(params[0]);
                userID = db.getUser(SecuraDBHelper.email, encPass);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            return String.valueOf(userID);
        }

        @Override
        protected void onPostExecute(String result) {
            AlertDialog alertDialog = new AlertDialog.Builder(ChangePassword.this).create();

            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            if(Integer.parseInt(result) == -1){
                alertDialog.setMessage("Incorrect old password");
                alertDialog.show();
                pd.dismiss();
            }
            else{
                pd.setMessage("Updating Password");
                String oldPass = ((EditText) findViewById(R.id.currentPassword)).getText().toString();
                String newPass = ((EditText) findViewById(R.id.newPassword)).getText().toString();
                new UpdatePassword().execute(oldPass, newPass);
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private class UpdatePassword extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            SecuraDBHelper db = new SecuraDBHelper(ChangePassword.this);
            boolean result = false;
            //System.out.println("DOINBACK");
            try {
                String oldPass = AESHelper.encrypt(params[0]);
                String newPass = AESHelper.encrypt(params[1]);
                result = db.updateUserPassword(SecuraDBHelper.email, oldPass, newPass);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            return String.valueOf(result);
        }

        @Override
        protected void onPostExecute(String result) {
            AlertDialog alertDialog = new AlertDialog.Builder(ChangePassword.this).create();

            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            if(!Boolean.valueOf(result)){
                alertDialog.setMessage("Something went wrong");
                alertDialog.show();
            }
            else{
                setContentView(R.layout.layout_passwordchangesuccess);
            }
            pd.dismiss();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
