package com.secura.ankit.secura.utils;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.secura.ankit.secura.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ankit on 15/7/16.
 */
public class DataParser {

    /**
     *
     * @param view
     * @return
     * Method returns the child elements as keyValue pair
     */
    public static ArrayList<HashMap<String, String>> llToMap(View view){
        ArrayList<HashMap<String, String>> layoutData = new ArrayList<>();
        LinearLayout ll = (LinearLayout) view.findViewById(view.getId());
        int childcount = ll.getChildCount();
        for (int i=0; i < childcount; i++){
            View childView = ll.getChildAt(i);
            if(childView instanceof LinearLayout){
                ArrayList<HashMap<String, String>> childData = llToMap(childView);
                System.out.println(" LL DATA : " + childData);
                for(HashMap<String, String> row : childData){
                    layoutData.add(row);
                }
            }
            else{
                System.out.println("INELSE : ChildView : " + childView);
                if(childView instanceof TextView){
                    System.out.println("Yes it is instance of TextView");
                    HashMap<String, String> elem = new HashMap<>();
                    if(ll.getChildAt(i+1) instanceof EditText){
                        elem.put(((TextView) childView).getText().toString(), ((EditText) ll.getChildAt(i+1)).getText().toString());
                        i++;
                        layoutData.add(elem);

                    }
                    else if(ll.getChildAt(i+1) instanceof Spinner){
                        elem.put(((TextView) childView).getText().toString(), ((Spinner) ll.getChildAt(i+1)).getSelectedItem().toString());
                        i++;
                        layoutData.add(elem);
                    }
                }
            }
        }
        return layoutData;
    }

    public static String convertToJSON(ArrayList<HashMap<String, String>> data){
        JSONObject jArrayItemData = new JSONObject();
        String key, value;
        //JSONObject jObjectType = new JSONObject();
        try {
            for(HashMap<String, String> row: data){
                key = row.entrySet().iterator().next().getKey();
                value = row.entrySet().iterator().next().getValue();
                //System.out.println("ROW : " + row.entrySet().iterator().next().getKey());
                jArrayItemData.put(key, value);
            }
            //jObjectType.put("type", "facebook_login");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jArrayItemData.toString();
    }
}
