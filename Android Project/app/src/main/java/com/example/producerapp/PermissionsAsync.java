package com.example.producerapp;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PermissionsAsync {
    MainActivity da = null;
    String did = null;

    public void getLogs(String did, MainActivity da) {
        this.da = da;
        this.did = did;
        new AsyncFlickrSearch().execute(did);
    }

    private class AsyncFlickrSearch extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            System.out.println("In doInBackground");
            return search(urls[0]);
        }

        protected void onPostExecute(String results) {
            da.updatePermissions(results, did);
        }

        private String search(String searchWord) {
            String jsonString = getRemoteJSON("http://128.237.116.103:8082/permissions?did="+searchWord);
            return jsonString;
        }
    }

    private String getRemoteJSON(String urlstring) {
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
            System.out.println("Response in PermissionAsync: " + response.toString());
            return response.toString();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
