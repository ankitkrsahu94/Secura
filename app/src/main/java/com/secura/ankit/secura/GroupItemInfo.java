package com.secura.ankit.secura;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.secura.ankit.secura.DatabaseHelper.SecuraDBHelper;

/**
 * Created by ankit on 13/7/16.
 */
public class GroupItemInfo extends Activity {

    String result;
    TextView tv;
    LinearLayout ll;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupiteminfo);

        ll = (LinearLayout) findViewById(R.id.groupItemInfoLayout);
        tv = new TextView(this);

        Intent intent = getIntent();
        pd = new ProgressDialog(this);
        pd.show();
        new FetchItemInfo().execute(String.valueOf(intent.getIntExtra("map_ID", -100)));

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
            System.out.println("Fuck InDATA : " + result);
            tv.setText(result);
            ll.addView(tv);
            pd.dismiss();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
