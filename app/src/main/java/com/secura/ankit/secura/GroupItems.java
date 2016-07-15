package com.secura.ankit.secura;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.secura.ankit.secura.DatabaseHelper.SecuraDBHelper;
import com.secura.ankit.secura.utils.DataParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.zip.Inflater;

public class GroupItems extends AppCompatActivity {

    ArrayList<String> list = new ArrayList<String>();
    ProgressDialog pd;
    ListView listView;
    int groupID, infoCategory;
    View dialogView;
    LinkedHashMap<Integer, String> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        final Intent intent = getIntent();
        groupID = intent.getIntExtra("groupID", -1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.accountList);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(GroupItems.this, groupID+"", Toast.LENGTH_SHORT).show();
                createNewGroupItemDialog();
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
                Toast.makeText(GroupItems.this, listView.getItemAtPosition(position)+" info", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), GroupItemInfo.class);
                intent.putExtra("map_ID", Integer.parseInt(result.keySet().toArray()[position].toString()));
                startActivity(intent);
                /*Intent intent = new Intent(getApplicationContext(), GroupItems.class);
                intent.putExtra("groupID", 1);
                startActivity(intent);
                Toast.makeText(GroupItems.this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();*/
            }
        });

        pd = new ProgressDialog(this);
        pd.setMessage("Fetching Data");
        pd.show();
        //groupID = Integer.valueOf(intent.getIntExtra("groupID", -1));
        //Toast.makeText(GroupItems.this, String.valueOf(groupID), Toast.LENGTH_SHORT).show();
        new FetchItems().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pd.dismiss();
    }

    private void createNewGroupItemDialog(){
        //Toast.makeText(GroupItems.this, groupID, Toast.LENGTH_SHORT).show();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GroupItems.this);
        alertDialog.setTitle("Add new item");
        //alertDialog.setMessage("Title");

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(GroupItems.this.LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.newgroupitem, null);
        alertDialog.setView(dialogView);

        Spinner sp = (Spinner) dialogView.findViewById(R.id.itemType);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        dialogView.findViewById(R.id.cardData).setVisibility(View.GONE);
                        dialogView.findViewById(R.id.loginData).setVisibility(View.GONE);
                        dialogView.findViewById(R.id.internetBanking).setVisibility(View.GONE);
                        infoCategory = 0;
                        break;
                    case 1:
                        dialogView.findViewById(R.id.cardData).setVisibility(View.GONE);
                        dialogView.findViewById(R.id.loginData).setVisibility(View.VISIBLE);
                        dialogView.findViewById(R.id.internetBanking).setVisibility(View.GONE);
                        infoCategory = 1;
                        break;
                    case 2:
                        dialogView.findViewById(R.id.loginData).setVisibility(View.GONE);
                        dialogView.findViewById(R.id.cardData).setVisibility(View.VISIBLE);
                        dialogView.findViewById(R.id.internetBanking).setVisibility(View.GONE);
                        infoCategory = 2;
                        break;
                    case 3:
                        dialogView.findViewById(R.id.loginData).setVisibility(View.GONE);
                        dialogView.findViewById(R.id.cardData).setVisibility(View.GONE);
                        dialogView.findViewById(R.id.internetBanking).setVisibility(View.VISIBLE);
                        infoCategory = 3;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*final EditText input = new EditText(getApplicationContext());
        input.setTextColor(Color.parseColor("#000000"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);*/
        //alertDialog.setIcon(R.drawable.key);

        alertDialog.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(infoCategory == -1){
                            Toast.makeText(GroupItems.this, "No Data was provided", Toast.LENGTH_SHORT).show();

                        }
                        else{
                            pd.setMessage("Creating new group item");
                            ArrayList<HashMap<String, String>> filledData = new ArrayList<HashMap<String, String>>();
                            LinearLayout ll = (LinearLayout) dialogView.findViewById(R.id.itemData);
                            String jsonData, title;

                            filledData.addAll(DataParser.llToMap(ll.getChildAt(0)));
                            filledData.addAll(DataParser.llToMap(ll.getChildAt(infoCategory)));
                            jsonData = DataParser.convertToJSON(filledData);
                            //System.out.println("View : " + ll.getChildAt(infoCategory));
                            System.out.println("Parsed Data : " + filledData);
                            System.out.println("JSON DATA : " + jsonData);
                            title = ((EditText)dialogView.findViewById(R.id.itemTitle)).getText().toString();
                            if(title.equals("") || title.equals(" ")){
                                Toast.makeText(GroupItems.this, "Please enter title for this item", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                new CreateGroupItem().execute(jsonData, title);
                                dialog.cancel();
                            }
                        }
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

    private class CreateGroupItem extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            SecuraDBHelper db = new SecuraDBHelper(getApplicationContext());

            /*JSONObject jArrayItemData = new JSONObject();
            JSONObject jObjectType = new JSONObject();
            try {
                jObjectType.put("type", "facebook_login");
                jArrayItemData.put("system", jObjectType);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            // put elements into the object as a key-value pair
            //Toast.makeText(GroupItems.this, groupID+"in", Toast.LENGTH_SHORT).show();
            //pd.dismiss();
            //Log.e("Fuck : GroupID", groupID+"");
            try {
                db.insertItem(groupID, params[1], params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //group_list = db.getItems();
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            new FetchItems().execute();
            pd.dismiss();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private class FetchItems extends AsyncTask<String, Void, String> {

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
            result = db.getItems(groupID);
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
            pd.dismiss();
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
        new FetchItems().execute("");
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
