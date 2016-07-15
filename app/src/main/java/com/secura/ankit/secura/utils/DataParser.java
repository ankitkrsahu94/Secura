package com.secura.ankit.secura.utils;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.secura.ankit.secura.R;

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
        //pd = new ProgressDialog(GroupItems.this);
        int childcount = ll.getChildCount();
        //Log.e("YOUO", ll.getChildAt(infoCategory)+"");
        //Log.e("Length : ", childcount +"");
        for (int i=0; i < childcount; i++){
            View childView = ll.getChildAt(i);
            System.out.println("ChildView : " + childView);
            if(childView instanceof LinearLayout){
                for(HashMap<String, String> row : llToMap(childView)){
                    layoutData.add(row);
                }
            }
            else{
                if(childView instanceof TextView){
                    if(ll.getChildAt(i+1) instanceof EditText) {
                        HashMap<String, String> elem = new HashMap<>();
                        elem.put(((TextView) childView).getText().toString(), ((EditText) ll.getChildAt(i+1)).getText().toString());
                        i++;
                    }
                    else{
                        return new ArrayList<HashMap<String, String>>() {{
                            add(0, new HashMap<String, String>(){{put("error", "-1");}});
                        }};
                    }
                }
            }
        }
        return layoutData;
    }
}
