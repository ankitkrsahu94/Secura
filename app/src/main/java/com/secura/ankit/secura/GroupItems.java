package com.secura.ankit.secura;

/**
 * This activity is not being used anymore
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.secura.ankit.secura.DatabaseHelper.SecuraDBHelper;
import com.secura.ankit.secura.utils.AESHelper;
import com.secura.ankit.secura.utils.DataParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class GroupItems extends BaseAppCompatActivity {

    ArrayList<String> list = new ArrayList<String>();
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

                pd = new ProgressDialog(GroupItems.this);
                pd.show();
                new FetchItemInfo().execute(result.keySet().toArray()[position].toString());

                /*Intent intent = new Intent(getApplicationContext(), GroupItemInfo.class);
                intent.putExtra("map_ID", Integer.parseInt(result.keySet().toArray()[position].toString()));
                startActivity(intent);*/

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
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(GroupItems.this.LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.newgroupitem, null);
        final AlertDialog d = new AlertDialog.Builder(GroupItems.this)
                .setView(dialogView)
                .setTitle("Add new item")
                .setPositiveButton("Save", null) //Set to null. We override the onclick
                .setNegativeButton("Cancel", null)
                .create();

        /*final EditText pwd = (EditText) findViewById(R.id.password);
        final CheckBox cb = (CheckBox) findViewById(R.id.showPassword);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cb.isChecked()){
                    pwd.setTransformationMethod(null);
                }
                else{
                    pwd.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });*/

        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        if(infoCategory == 0){
                            Toast.makeText(GroupItems.this, "Please select a category", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            LinearLayout ll = (LinearLayout) dialogView.findViewById(R.id.itemData);
                            ArrayList<HashMap<String, String>> filledData = new ArrayList<>();
                            String jsonData, title;
                            title = ((EditText)dialogView.findViewById(R.id.itemTitle)).getText().toString();
                            if(title.equals("") || title.equals(" ")){
                                Toast.makeText(GroupItems.this, "Please enter title for this item", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                filledData.addAll(DataParser.llToMap(ll.getChildAt(0)));
                                filledData.addAll(DataParser.llToMap(ll.getChildAt(infoCategory)));
                                jsonData = DataParser.convertToJSON(filledData);
                                System.out.println("Parsed Data : " + filledData);
                                System.out.println("JSON DATA : " + jsonData);
                                pd.setMessage("Creating new group item");
                                new CreateGroupItem().execute(jsonData, title);
                                dialog.dismiss();
                            }
                        }
                    }
                });
            }
        });
        d.show();

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
    }

    private class CreateGroupItem extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            SecuraDBHelper db = new SecuraDBHelper(getApplicationContext());

            try {
                db.insertItem(groupID, params[1], params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

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
            SecuraDBHelper db = new SecuraDBHelper(getApplicationContext());

            /**
             * since all the group information is returned as <key,value> pair
             */
            result = db.getItems(groupID);
            list.clear();
            for (Object value : result.values()) {
//                Log.e("D ID : ", value.toString());
                list.add(value.toString());
            }

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

    private class FetchItemInfo extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            /*try{
                getApplicationContext().deleteDatabase(SecuraDBHelper.DATABASE_NAME);
            }catch (Exception e){
                e.printStackTrace();
            }*/
            String result;
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
            AlertDialog alertDialog = new AlertDialog.Builder(GroupItems.this).create();
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
            alertDialog.setMessage(finalData);
            alertDialog.show();
            pd.dismiss();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public void listItemClickListener(ArrayAdapter adapter, View v, int position,
                                      long arg3) {
        Toast.makeText(getApplicationContext(), adapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(GroupItems.this, Dashboard.class);
        startActivity(intent);
    }
}
