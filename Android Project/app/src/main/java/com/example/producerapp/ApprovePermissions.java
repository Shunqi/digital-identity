package com.example.producerapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class ApprovePermissions extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.approve_permissions);

        /*ListView listView = (ListView) findViewById(R.id.permissions_listView);
        ArrayList<String> permissionsList = new ArrayList(Arrays.asList("Health Records", "Bank Details", "Shopping Transactions"));

        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.activity_list_item, permissionsList);
        listView.setAdapter(adapter);*/


    }

}
