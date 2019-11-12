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
        //dashboard_textview.setText(getLogs());
    }

    private String getLogs() {
        StringBuilder results = new StringBuilder();

        //Build the URL string and retrieve the object from the server
        JSONObject jobj = getRemoteJSON("urlstring here");
        JSONArray arr = null;

        //Parse through the array in the json object and build the results string
        try {
            arr = jobj.getJSONArray("Extracts");
            if(arr.length() == 0)
                return "Sorry no results available!";

            for(int i=0; i<arr.length(); i++){
                //Append author name from the array
                results.append(arr.getJSONObject(i).getString("author"));
                results.append("\n");
            }
            //Return the results string
            return results.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Return an empty string if an exception occurs
        return "";
    }

    private JSONObject getRemoteJSON(String urlstring) {

        try {
            //Create a url from the string provided
            URL url = new URL(urlstring);
            //Open a connection to the url
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            //Read the data sent from the server
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println(response.toString());
            JSONObject jobj = new JSONObject(response.toString());

            //Return the json object built from the response string
            return jobj;
        }
        //Catch all possible exceptions
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Return null if an exception is caught
        return null;
    }
}
