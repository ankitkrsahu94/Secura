package com.secura.ankit.secura;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.secura.ankit.secura.DatabaseHelper.SecuraDBHelper;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    String[] mobileArray = {"Android","IPhone"};
    ArrayList<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        /*for(int i=0; i <mobileArray.length; i++){
            list.add(mobileArray[i]);
        }*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        try{
            this.deleteDatabase(SecuraDBHelper.DATABASE_NAME);
        }catch (Exception e){
            e.printStackTrace();
        }
        SecuraDBHelper db = new SecuraDBHelper(this);

        db.insertItem("Allahabad Bank");
        db.insertItem("Axis Bank");
        list = db.getItems();
        db.close();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.listrowview, list);

        ListView listView = (ListView) findViewById(R.id.accountList);
        listView.setAdapter(adapter);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
