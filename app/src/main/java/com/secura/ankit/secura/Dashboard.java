package com.secura.ankit.secura;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.secura.ankit.secura.DatabaseHelper.SecuraDBHelper;
import com.secura.ankit.secura.DatabaseSchema.GroupItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Dashboard extends AppCompatActivity {

    ArrayList<String> list = new ArrayList<String>();
    ProgressDialog pd;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.accountList);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewGroupDialog();
                /*Intent intent = new Intent(getApplicationContext(), NewItemActivity.class);
                intent.putStringArrayListExtra("GROUP_LIST", list);
                startActivityForResult(intent,1);*/
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), GroupItems.class);
                intent.putExtra("groupID", )
                Toast.makeText(Dashboard.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        pd = new ProgressDialog(this);
        pd.setMessage("Fetching Data");
        pd.show();

        new FetchGroups().execute("");
    }

    private void createNewGroupDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Dashboard.this);
        alertDialog.setTitle("Create new group");
        alertDialog.setMessage("Group Name");

        final EditText input = new EditText(getApplicationContext());
        input.setTextColor(Color.parseColor("#000000"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        //alertDialog.setIcon(R.drawable.key);

        alertDialog.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pd = new ProgressDialog(Dashboard.this);
                        pd.setMessage("Creating new group");
                        pd.show();
                        new CreateGroup().execute(input.getText().toString());
                        dialog.cancel();
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private class CreateGroup extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            SecuraDBHelper db = new SecuraDBHelper(getApplicationContext());
            db.createGroupMap(params[0], SecuraDBHelper.email);
            //group_list = db.getItems();
            return params[0];
        }

        @Override
        protected void onPostExecute(String result) {
            new FetchGroups().execute();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private class FetchGroups extends AsyncTask<String, Void, String> {

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
            LinkedHashMap<Integer, String> result = db.getGroups(SecuraDBHelper.email);
            list.clear();
            for (Object value : result.values()) {
                //Log.e("D ID : ", value.toString());
                list.add(value.toString());
                // write your code here
            }
            //list = db.getGroups(SecuraDBHelper.email);
            //db.close();
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.listrowview, list);
            listView.setAdapter(adapter);
            pd.hide();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new FetchGroups().execute("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void listItemClickListener(ArrayAdapter adapter, View v, int position,
                                      long arg3) {
        Toast.makeText(getApplicationContext(), adapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
    }
}
