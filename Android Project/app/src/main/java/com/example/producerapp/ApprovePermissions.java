package com.example.producerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;
import java.util.ArrayList;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ApprovePermissions extends Activity {

    ArrayList<PermissionItem> expandableList;
    HashMap<PermissionItem, List<ThirdPartyItem>> items;
    PermissionExpandableAdapter boxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approve_permissions);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        intent.removeExtra("permissions");
        System.out.println("Extras in Approve permissions: "+extras.getString("permissions"));

        items = createHashMap(extras.getString("permissions"));
        expandableList = createList(items);

        ExpandableListView lvMain = (ExpandableListView) findViewById(R.id.permissions_listView);
        boxAdapter = new PermissionExpandableAdapter(this, expandableList,items);
        lvMain.setAdapter(boxAdapter);

        lvMain.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                System.out.println(expandableList.get(groupPosition) + " List Expanded.");
            }
        });

        lvMain.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                System.out.println(expandableList.get(groupPosition) + " List Collapsed.");

            }
        });

        lvMain.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        expandableList.get(groupPosition)
                                + " -> "
                                + items.get(
                                expandableList.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        });
        Button approveButton = (Button) findViewById(R.id.approve_button);
        approveButton.setVisibility(View.VISIBLE);
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String result = createJSON(boxAdapter);
                System.out.println("Resultant json string is : " +result);
                notifyServer(result, "YES");

                Intent i = new Intent(ApprovePermissions.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        Button rejectButton = (Button) findViewById(R.id.reject_button);
        rejectButton.setVisibility(View.VISIBLE);
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject jsonResponse = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                jsonResponse.put("permissions",jsonArray);
                String result = jsonResponse.toJSONString();
                notifyServer(result, "NO");

                Intent i = new Intent(ApprovePermissions.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        Button updateButton = (Button) findViewById(R.id.update_button);
        updateButton.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }

    public void notifyServer(String result, String message){
        Random random = new Random();
        final String SENDER_ID = "923983506811"; //Sender ID from Firebase Console
        final int messageId = random.nextInt(); // Increment for each
        // [START fcm_send_upstream]
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
                .setMessageId(Integer.toString(messageId))
                .addData("message", message)
                .addData("route", "Permissions")
                .addData("approved_permissions", result)
                .build());
        System.out.println("Message sent to server");
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
                if(current.get("thirdPartyDIDs") != null)
                if( !((String)current.get("thirdPartyDIDs")).equalsIgnoreCase("")){
                    String[] thirdpartyArr = ((String) current.get("thirdPartyDIDs")).split(",");

                    for(int i=0; i<thirdpartyArr.length; i++){
                        ThirdPartyItem t = new ThirdPartyItem(thirdpartyArr[i],true);
                        mylist.add(t);
                    }
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

    public String createJSON(PermissionExpandableAdapter boxAdapter){

        JSONObject jsonResponse = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        //getBox returns arraylist for now, make changes later to add third parties to the json
        HashMap<PermissionItem, List<ThirdPartyItem>> myMap = boxAdapter.getBox();
        //for (PermissionItem p : boxAdapter.getBox()) {
        for (Map.Entry<PermissionItem, List<ThirdPartyItem>> p : myMap.entrySet()) {
            JSONObject newEntry = new JSONObject();
            newEntry.put("category", p.getKey().category);
            newEntry.put("read", p.getKey().readbox);
            newEntry.put("write", p.getKey().writebox);
            newEntry.put("shareable", p.getKey().sharebox);

            ArrayList<ThirdPartyItem> arr = (ArrayList<ThirdPartyItem>) p.getValue();
            String dids = "";
            for(int i=0; i<arr.size(); i++)
                if(arr.get(i).allowedbox)
                    dids += arr.get(i).thirdPartyName+",";

            newEntry.put("thirdPartyDIDs", dids);
            jsonArray.add(newEntry);
        }
        jsonResponse.put("permissions",jsonArray);

        return jsonResponse.toJSONString();
    }

}
