package com.example.producerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RevokePermissions extends Activity {

    ArrayList<PermissionItem> items;
    ListAdapter boxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.revoke_permissions);

        ImageButton back_button = (ImageButton) findViewById(R.id.revoke_back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RevokePermissions.this, MainActivity.class);
                startActivity(i);
                finish();

            }
        });

        Button search_DID_button = (Button) findViewById(R.id.search_did_button);
        search_DID_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText textInputLayout = (TextInputEditText) findViewById(R.id.textInput);
                //String jsonString = fetchJSONstring(textInputLayout.getEditText().getText().toString());
                System.out.println("You typed: "+textInputLayout.getText().toString());
                textInputLayout.getText().clear();

                /*items = createArrayList(jsonString);
                ListView lvMain = (ListView) findViewById(R.id.permissions_listView);
                boxAdapter = new ListAdapter(this, items);
                lvMain.setAdapter(boxAdapter);*/
            }
        });

    }

    public String fetchJSONstring(String searchword){
        return null;
    }
    public ArrayList<PermissionItem> createArrayList(String jsonStr){
        try {
            JSONParser parser = new JSONParser() {};
            JSONObject jsonObject = (JSONObject) parser.parse(jsonStr);
            JSONArray jsonArray = (JSONArray) jsonObject.get("permissions");
            System.out.println(jsonArray.toString());

            ArrayList<PermissionItem> items = new ArrayList<>();

            Iterator<JSONObject> iterator = jsonArray.iterator();
            while(iterator.hasNext()) {
                JSONObject current = iterator.next();
                PermissionItem p = new PermissionItem((String) current.get("category"),
                        (Boolean) current.get("read"),
                        (Boolean) current.get("write"),
                        (Boolean) current.get("shareable"));
                items.add(p);

            }
            return items;
        } catch (ParseException ex) {
            Logger.getLogger(ApprovePermissions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
