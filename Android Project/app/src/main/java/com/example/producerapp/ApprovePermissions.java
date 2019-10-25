package com.example.producerapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class ApprovePermissions extends Activity {

    ArrayList<PermissionItem> items = new ArrayList<PermissionItem>();
    ListAdapter boxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approve_permissions);

        ArrayList<PermissionItem> items = new ArrayList();
        items.add(new PermissionItem("Health Records", false));
        items.add(new PermissionItem("Bank Details", false));
        items.add(new PermissionItem("Shopping Transactions", false));

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
