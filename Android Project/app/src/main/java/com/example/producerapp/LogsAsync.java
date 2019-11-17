package com.example.producerapp;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LogsAsync {

    MainActivity da = null;

    public void getLogs(MainActivity da) {
        this.da = da;
        String a = "pp";
        new AsyncFlickrSearch().execute(a);
    }


    private class AsyncFlickrSearch extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            System.out.println("In doInBackground");
            return search(urls[0]);
        }

        protected void onPostExecute(String results) {
            da.showResults(results);
        }


        private String search(String searchWord) {
            StringBuilder results = new StringBuilder();
            JSONArray arr = getRemoteJSON("http://128.237.116.103:8082/logs");

            try {

                if(arr.length() == 0)
                    return "Sorry no results available!";

                for(int i=0; i<arr.length(); i++){
                    results.append("DID: "+arr.getJSONObject(i).getString("DID")+"\n");
                    results.append("Timestamp: "+arr.getJSONObject(i).getString("timestamp")+"\n");
                    results.append("Type: "+arr.getJSONObject(i).getString("type")+" ");
                    results.append("Route: "+arr.getJSONObject(i).getString("route")+"\n");
                    results.append("Status: "+arr.getJSONObject(i).getString("status")+"\n");
                    results.append("Message: "+arr.getJSONObject(i).getString("message"));
                    results.append("\n\n");
                }
                System.out.println("in async search result string: "+ results.toString());
                return results.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return "";
        }
    }

    private JSONArray getRemoteJSON(String urlstring) {
        System.out.println("In getRemoteJSON");

        try {

            URL url = new URL(urlstring);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println(response.toString());
            JSONArray jarr = new JSONArray(response.toString());

            return jarr;
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Return null if an exception is caught
        JSONArray jarr = new JSONArray();
        return jarr;
    }
}
