package com.example.producerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button dashboard_button = (Button) findViewById(R.id.dashboard_button);
        dashboard_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLogs();
            }
        });

        Button update_button = (Button) findViewById(R.id.update_button);
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText textInputLayout = (TextInputEditText) findViewById(R.id.textInput);
                String did = textInputLayout.getText().toString();
                textInputLayout.getText().clear();
                System.out.println("DID: "+did);
                getPermissionSet(did);
            }
        });

    }
    private void getLogs() {
        LogsAsync la = new LogsAsync();
        la.getLogs(this);
    }

    public void showResults(String results){
        System.out.println("showresults: "+results);
        Intent i = new Intent(MainActivity.this, Dashboard.class);
        i.putExtra("results", results);
        startActivity(i);
        finish();

    }

    private void getPermissionSet(String did) {
        PermissionsAsync pa = new PermissionsAsync();
        System.out.println("GetPermissionSet did: "+did);
        pa.getLogs(did, this);
    }

    public void updatePermissions(String jsonString, String did){
        System.out.println("MainActivity jsonString: "+ jsonString);
        Intent i = new Intent(MainActivity.this, UpdatePermissions.class);
        i.putExtra("jsonString", jsonString);
        i.putExtra("did", did);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
