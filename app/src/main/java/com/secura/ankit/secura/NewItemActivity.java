package com.secura.ankit.secura;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;

/**
 * Created by Vassar-Dell-4 on 03-Jul-16.
 */
public class NewItemActivity extends Activity {

    ArrayList<String> group_list = new ArrayList<>();
    ProgressDialog pd;
    Spinner sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newitemlayout);

        Button save = (Button) findViewById(R.id.saveItem);

        final CheckBox cb = (CheckBox) findViewById(R.id.showPassword);
        final EditText pwd = (EditText) findViewById(R.id.password);

        Bundle extras = getIntent().getExtras();
        group_list = extras.getStringArrayList("GROUP_LIST");
        group_list.add(group_list.size(), " + Create new group");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, group_list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp = (Spinner) findViewById(R.id.spinner);
        sp.setAdapter(adapter);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(group_list.size() == position+1){
                    sp.setSelection(0);
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewItemActivity.this);
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
                                    pd = new ProgressDialog(NewItemActivity.this);
                                    pd.setMessage("Creating new group");
                                    pd.show();
                                    new CreateGroup().execute(input.getText().toString());
                                    dialog.cancel();
                                    Toast.makeText(getApplicationContext(), "hey", Toast.LENGTH_SHORT).show();
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewItemActivity.this.finish();
            }
        });
    }

    private class CreateGroup extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            SecuraDBHelper db = new SecuraDBHelper(getApplicationContext());
            //db.insertItem(params[0]);
            //group_list = db.getItems();
            db.close();
            return params[0];
        }

        @Override
        protected void onPostExecute(String result) {
//            Log.e("Fuck : ", group_list.toString());
            group_list.add(group_list.size()-1,result);
            sp.setSelection(group_list.indexOf(result));
            pd.hide();
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
