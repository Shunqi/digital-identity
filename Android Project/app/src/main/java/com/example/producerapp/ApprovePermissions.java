package com.example.producerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import java.util.logging.Level;
import org.bson.Document;


public class ApprovePermissions extends Activity {

    ArrayList<PermissionItem> items;
    ListAdapter boxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approve_permissions);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        intent.removeExtra("permissions");

        System.out.println("Extras in Approve permissions: "+extras.getString("permissions"));
        String[] permissionsArray = extras.getString("permissions").split(",");

        items = new ArrayList<PermissionItem>();
        for(int i=0; i<permissionsArray.length; i++) {
            items.add(new PermissionItem(permissionsArray[i], false));
        }

        ListView lvMain = (ListView) findViewById(R.id.permissions_listView);
        boxAdapter = new ListAdapter(this, items);
        lvMain.setAdapter(boxAdapter);

        Button approveButton = (Button) findViewById(R.id.approve_button);
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Approve button clicked");
                String result = "You selected: ";
                for (PermissionItem p : boxAdapter.getBox()) {
                    if (p.box) {
                        result += " " + p.title;

                    }
                }
                System.out.println(result +"\n");
                //saveJSON(result);

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

    public void saveJSON(String permissions){
        java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);

        MongoClientURI uri = new MongoClientURI("");

        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("");
        MongoCollection<Document> collection = database.getCollection("");

        Document doc = new Document("producer_DID", "")
                .append("consumer_DID", "")
                .append("permissions", permissions)
                .append("access_control", "");
                //.append("access_control", new Document("x", 203).append("y", 102));

        collection.insertOne(doc);

    }
}
