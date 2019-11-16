package com.example.producerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdatePermissions extends Activity {

    ArrayList<PermissionItem> items;
    ListAdapter boxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approve_permissions);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        System.out.println("Extras in jsonString: "+extras.getString("jsonString"));
        items = createArrayList(extras.getString("jsonString"));
        final String did = extras.getString("did");

        for(int i=0; i<items.size(); i++){
            System.out.println(items.get(i).category+ items.get(i).readbox+items.get(i).writebox+items.get(i).sharebox);
        }

        ListView lvMain = (ListView) findViewById(R.id.permissions_listView);
        boxAdapter = new ListAdapter(this, items);
        lvMain.setAdapter(boxAdapter);

        Button approveButton = (Button) findViewById(R.id.approve_button);
        approveButton.setVisibility(View.INVISIBLE);

        Button rejectButton = (Button) findViewById(R.id.reject_button);
        rejectButton.setVisibility(View.INVISIBLE);

        Button updateButton = (Button) findViewById(R.id.update_button);
        updateButton.setVisibility(View.VISIBLE);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = createJSON(boxAdapter, did);
                System.out.println("Resultant json string is : " +result);
                notifyServer(result);
            }
        });

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

    public String createJSON(ListAdapter boxAdapter, String did){

        JSONObject jsonResponse = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        jsonResponse.put("consumerDID", did);
        for (PermissionItem p : boxAdapter.getBox()) {
            JSONObject newEntry = new JSONObject();
            newEntry.put("category", p.category);
            newEntry.put("read", p.readbox);
            newEntry.put("write", p.writebox);
            newEntry.put("shareable", p.sharebox);
            jsonArray.add(newEntry);
        }
        jsonResponse.put("permissions",jsonArray);

        return jsonResponse.toJSONString();
    }

    public void notifyServer(String result){
        UpdateAsync ua = new UpdateAsync();
        ua.writeToURL(result, this);
    }

    public void backToMain(){
        Intent i = new Intent(UpdatePermissions.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
