package com.example.producerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdatePermissions extends Activity {

    /*ArrayList<PermissionItem> items;
    PermissionListAdapter boxAdapter;*/

    ArrayList<PermissionItem> expandableList;
    HashMap<PermissionItem, List<ThirdPartyItem>> items;
    PermissionExpandableAdapter boxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approve_permissions);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        System.out.println("Extras in jsonString: "+extras.getString("jsonString"));

        items = createHashMap(extras.getString("jsonString"));
        expandableList = createList(items);
        final String did = extras.getString("did");

        ExpandableListView lvMain = (ExpandableListView) findViewById(R.id.permissions_listView);
        boxAdapter = new PermissionExpandableAdapter(this, expandableList, items);
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

    public HashMap<PermissionItem, List<ThirdPartyItem>> createHashMap(String jsonStr){
        try {
            JSONParser parser = new JSONParser() {};
            JSONObject jsonObject = (JSONObject) parser.parse(jsonStr);
            JSONArray jsonArray = (JSONArray) jsonObject.get("permissions");
            System.out.println(jsonArray.toString());

            HashMap<PermissionItem, List<ThirdPartyItem>> items = new HashMap<>();

            Iterator<JSONObject> iterator = jsonArray.iterator();
            while(iterator.hasNext()) {
                JSONObject current = iterator.next();
                PermissionItem p = new PermissionItem((String) current.get("category"),
                        (Boolean) current.get("read"),
                        (Boolean) current.get("write"),
                        (Boolean) current.get("shareable"));

                List<ThirdPartyItem> mylist = new ArrayList<>();
                Random r = new Random();
                int rint = 1+ r.nextInt(4);
                for(int i=0; i<rint; i++){
                    ThirdPartyItem t = new ThirdPartyItem(Integer.toString(i),false);
                    mylist.add(t);
                }
                System.out.println("Thirdparty list size: " + mylist.size());
                items.put(p, mylist);
            }
            return items;
        } catch (ParseException ex) {
            Logger.getLogger(ApprovePermissions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ArrayList<PermissionItem> createList(HashMap<PermissionItem, List<ThirdPartyItem>> hashmap){
        ArrayList<PermissionItem> box = new ArrayList<>();
        for (Map.Entry<PermissionItem, List<ThirdPartyItem>> mapElement : hashmap.entrySet()) {
            box.add(mapElement.getKey());
        }
        System.out.println("List from hashmap size:" + box.size());
        return box;
    }

    public String createJSON2(PermissionListAdapter boxAdapter, String did){

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

    public String createJSON(PermissionExpandableAdapter boxAdapter, String did){

        JSONObject jsonResponse = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        jsonResponse.put("consumerDID", did);
        //getBox returns arraylist for now, make changes later to add third parties to the json
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
