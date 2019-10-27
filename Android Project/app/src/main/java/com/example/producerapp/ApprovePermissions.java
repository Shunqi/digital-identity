package com.example.producerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class ApprovePermissions extends Activity {

    ArrayList<PermissionItem> items;
    ListAdapter boxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approve_permissions);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        System.out.println("Extras in Approve permissions: "+extras.getString("permissions"));
        String[] permissionsArray = extras.getString("permissions").split(",");

        items = new ArrayList<PermissionItem>();
        for(int i=0; i<permissionsArray.length; i++) {
            items.add(new PermissionItem(permissionsArray[i], false));
        }

        boxAdapter = new ListAdapter(this, items);

        ListView lvMain = (ListView) findViewById(R.id.permissions_listView);
        lvMain.setAdapter(boxAdapter);

        Button approveButton = (Button) findViewById(R.id.approve_button);
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Approve button clicked");
                String result = "You selected: ";
                for (PermissionItem p : boxAdapter.getBox()) {
                    if (p.box) {
                        result += "\n" + p.title;

                    }
                }
                System.out.println(result);

                Intent i = new Intent(ApprovePermissions.this, MainActivity.class);
                startActivity(i);


                //createJSON();
            }
        });

    }

    public void createJSON(){

        JSONObject jobj = new JSONObject();
        try {
            jobj.put("producer", "DID1");
            jobj.put("consumer", "DID2");
            jobj.put("attribute_block", "Health");
            jobj.put("access_control", "RW");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println();

    }

}
