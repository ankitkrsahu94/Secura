package com.secura.ankit.secura;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.secura.ankit.secura.DatabaseHelper.SecuraDBHelper;
import com.secura.ankit.secura.utils.AESHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.Iterator;

/**
 * Created by ankit on 13/7/16.
 */
public class GroupItemInfo extends Activity {

    String result;
    TextView tv;
    LinearLayout ll;
    ProgressDialog pd;

    //AESHelper u = new AESHelper();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupiteminfo);

        ll = (LinearLayout) findViewById(R.id.groupItemInfoLayout);
        tv = new TextView(this);

        AlertDialog alertDialog = new AlertDialog.Builder(GroupItemInfo.this).create();
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Edit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
        });
        alertDialog.setMessage("data here");
        alertDialog.show();
        /*Intent intent = getIntent();
        pd = new ProgressDialog(this);
        pd.show();
        new FetchItemInfo().execute(String.valueOf(intent.getIntExtra("map_ID", -100)));*/

    }

    private class FetchItemInfo extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            /*try{
                getApplicationContext().deleteDatabase(SecuraDBHelper.DATABASE_NAME);
            }catch (Exception e){
                e.printStackTrace();
            }*/
            SecuraDBHelper db = new SecuraDBHelper(getApplicationContext());
            //db.createGroupMap("Group 1", SecuraDBHelper.email);
            /*db.insertItem("Allahabad Bank");
            db.insertItem("Axis Bank");*/

            /**
             * since all the group information is returned as <key,value> pair
             */
            result = db.getItemInfo(Integer.valueOf(params[0]));
            //list = db.getGroups(SecuraDBHelper.email);
            //db.close();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
//            System.out.println("Fuck InDATA : " + result);
            String decrypted = null;
            String finalData = "";
            try {
                decrypted = AESHelper.decrypt(result);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(decrypted);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    finalData += Html.fromHtml("<b>" + key + " : </b>" + jsonObject.get(key) + "<br/>");
                    /*Object value = jsonObject.get(key);
                    System.out.println(value);*/
                } catch (JSONException e) {
                    // Something went wrong!
                }
            }
            tv.setText(finalData);
            ll.addView(tv);
            pd.dismiss();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
