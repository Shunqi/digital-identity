package com.example.producerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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


public class Dashboard extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        System.out.println(extras);

        ImageButton back_button = (ImageButton) findViewById(R.id.dashboard_back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Dashboard.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        TextView dashboard_textview = (TextView) findViewById(R.id.dashboard_textview);
        dashboard_textview.setText(extras.getString("results"));

    }




}
