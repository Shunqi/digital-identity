package com.example.producerapp;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UpdateAsync {

    UpdatePermissions ua = null;

    public void writeToURL(String result, UpdatePermissions ua) {
        System.out.println("In writetoURL: "+ result);
        this.ua = ua;
        new AsyncSearch().execute(result);
    }

    private class AsyncSearch extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            System.out.println("In UpdateAsync doInBackground");
            connectToURL(urls[0]);
            return "";
        }

        protected void onPostExecute(String str) {
            ua.backToMain();
        }

        private void connectToURL(String result) {

            try {
                System.out.println("result: "+result);
                ServerDetails urlstr = new ServerDetails();
                URL url = new URL(urlstr.urlString+"/update/permissions");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "text/plain;charset=UTF-8");
                con.setDoOutput(true);
                OutputStream out = con.getOutputStream();
                out.write(result.getBytes());
                out.flush();
                out.close();
                System.out.println("Response Code: "+con.getResponseCode());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
