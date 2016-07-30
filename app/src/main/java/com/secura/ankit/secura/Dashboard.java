package com.secura.ankit.secura;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
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
import com.secura.ankit.secura.utils.Messaging;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Dashboard extends BaseAppCompatActivity {

    ArrayList<String> list = new ArrayList<>();
    LinkedHashMap<Integer, String> result;
    ListView listView;
    String selectedGroupName;
    boolean doubleBackToExitPressedOnce = false;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.listrowview, list);
        listView = (ListView) findViewById(R.id.accountList);
        if (listView != null) {
            listView.setAdapter(adapter);
        }
        else{
            Toast.makeText(Dashboard.this, "Failed to create group list", Toast.LENGTH_SHORT).show();
        }

        listView.setLongClickable(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    groupActionDialog(0);
                    /*Intent intent = new Intent(getApplicationContext(), NewItemActivity.class);
                    intent.putStringArrayListExtra("GROUP_LIST", list);
                    startActivityForResult(intent,1);*/
                    /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();*/
                }
            });
        }

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedGroupName = listView.getItemAtPosition(position).toString();
                registerForContextMenu(listView);
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), GroupItems.class);
                intent.putExtra("groupID", Integer.parseInt(result.keySet().toArray()[position].toString()));
                pd.dismiss();
                startActivity(intent);
                finish();
            }
        });

        pd = new ProgressDialog(this);
        pd.setMessage("Fetching Data");
        pd.show();
        new FetchGroups().execute("");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()== R.id.accountList) {
//            info.position variable gives the position of the clicked element
//            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("Choose an action");
            String[] menuItems = {"Rename", "Delete"};
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = {"Rename", "Delete"};

        switch (menuItemIndex){
            case 0:
                groupActionDialog(1);
                break;
            case 1:
                groupActionDialog(2);
//                warningMessage("This action cannot be reverted. Are you sure you want to delete this ?", Dashboard.this);
                break;
        }
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = listView.getItemAtPosition(info.position).toString();

        Toast.makeText(Dashboard.this, menuItemName + " for " + listItemName, Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * 0 = new
     * 1 = update
     * 2 = delete
     */
    private void groupActionDialog(final int type){
        String boxTitle = null;
        String fieldTitle = null;
        String positiveButtonLabel = "Save";
        final String message = "Performing requested operation";

        switch (type){
            case 0:
                boxTitle = "Create New Group";
                fieldTitle = "Group Name";
                break;

            case 1:
                boxTitle = "Update Group Info";
                fieldTitle = "New Name";
                break;

            case 2:
                boxTitle = "Alert!!!";
                fieldTitle = "You are about to delete a group which will cause every element inside this group be removed permanently as well. Items once deleted cannot be reverted. Are you sure you want to delete this ?";
                positiveButtonLabel = "Delete";
                break;

        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Dashboard.this);
        alertDialog.setTitle(boxTitle);
        alertDialog.setMessage(fieldTitle);

        //for any other operation except for delete(2)
        final EditText input = new EditText(getApplicationContext());
        input.setTextColor(Color.parseColor("#000000"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        if(type != 2){
            alertDialog.setView(input);
        }

        alertDialog.setPositiveButton(positiveButtonLabel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pd = new ProgressDialog(Dashboard.this);
                        pd.setMessage(message);
                        pd.show();
                        if(type == 2){
                            new updateGroup().execute(String.valueOf(type));

                        }
                        else{
                            new updateGroup().execute( String.valueOf(type), input.getText().toString());
                        }
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

    private class updateGroup extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            SecuraDBHelper db = new SecuraDBHelper(getApplicationContext());
            boolean success = false;
            int type = Integer.valueOf(params[0]);
            switch(type){
                case 0:
                    success = db.createGroup(params[1], SecuraDBHelper.email);
                    break;
                case 1:
                    success = db.updateGroupInfo(selectedGroupName, params[1]);
                    break;
                case 2:
                    success = db.removeGroup(selectedGroupName);
                    break;
            }

            return String.valueOf(success);
        }

        @Override
        protected void onPostExecute(String result) {
            if(!Boolean.valueOf(result)){
                Messaging.errorMessage("Operation failed", Dashboard.this);
            }
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
            SecuraDBHelper db = new SecuraDBHelper(getApplicationContext());

            /**
             * since all the group information is returned as <key,value> pair
             */
            result = db.getGroups(SecuraDBHelper.email);
            list.clear();
            for (Object value : result.values()) {
                list.add(value.toString());
            }
            db.close();
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            adapter.notifyDataSetChanged();
            pd.dismiss();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        new FetchGroups().execute("");
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pd.dismiss();
    }
}
