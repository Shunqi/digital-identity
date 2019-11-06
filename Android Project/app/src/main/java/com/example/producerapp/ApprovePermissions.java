package com.example.producerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ApprovePermissions extends Activity {

    ArrayList<PermissionItem> items;
    ListAdapter boxAdapter;
    final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approve_permissions);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        intent.removeExtra("permissions");

        System.out.println("Extras in Approve permissions: "+extras.getString("permissions"));
        items = createArrayList(extras.getString("permissions"));

        for(int i=0; i<items.size(); i++){
            System.out.println(items.get(i).category+ items.get(i).readbox+items.get(i).writebox+items.get(i).sharebox);
        }

        ListView lvMain = (ListView) findViewById(R.id.permissions_listView);
        boxAdapter = new ListAdapter(this, items);
        lvMain.setAdapter(boxAdapter);

        Button approveButton = (Button) findViewById(R.id.approve_button);
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String result = createJSON(boxAdapter);
                System.out.println("Resultant json string is : " +result);
                notifyServer(result);

                Intent i = new Intent(ApprovePermissions.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
    }

    public ArrayList<PermissionItem> createArrayList(String jsonStr){
        try {
            JSONParser parser = new JSONParser() {};
            JSONObject jsonObject = (JSONObject) parser.parse(jsonStr);
            JSONArray jsonArray = (JSONArray) jsonObject.get("permissions");
            System.out.println(jsonArray.toString());

            ArrayList<PermissionItem> items = new ArrayList<>();
            //Iterating the contents of the array
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

    public String createJSON(ListAdapter boxAdapter){

        JSONObject jsonResponse = new JSONObject();
        JSONArray jsonArray = new JSONArray();

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
        Random random = new Random();
        final String SENDER_ID = "923983506811"; //Sender ID from Firebase Console
        final int messageId = random.nextInt(); // Increment for each
        // [START fcm_send_upstream]
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
                .setMessageId(Integer.toString(messageId))
                .addData("route", "Permissions")
                .addData("approved_permissions", result)
                .build());
        System.out.println("Message sent to server");
    }

}
