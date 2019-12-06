package com.example.producerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class Dashboard extends AppCompatActivity {

    ArrayList<LogItem> items;
    LogsListAdapter boxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        System.out.println(extras);

        items = createArrayList(extras.getString("results"));

        ListView lvMain = (ListView) findViewById(R.id.logs_listView);
        boxAdapter = new LogsListAdapter(this, items);
        lvMain.setAdapter(boxAdapter);

    }

    private ArrayList<LogItem> createArrayList(String results) {
        ArrayList<LogItem> items = new ArrayList<>();
        String[] arr = results.split("\n\n");
        for(int i=0; i<arr.length; i++){
            LogItem item = new LogItem(arr[i]);
            items.add(item);
        }
        return items;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            Intent i = new Intent(Dashboard.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
