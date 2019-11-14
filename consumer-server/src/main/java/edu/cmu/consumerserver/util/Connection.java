package edu.cmu.consumerserver.util;

import com.google.api.Http;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.stream.Collectors;

public class Connection {
    public byte[] getByteArray(HttpURLConnection conn) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (InputStream inputStream = conn.getInputStream()) {
            int n;
            byte[] buffer = new byte[1024];
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
        }
        return output.toByteArray();
    }

    public String inputStream(HttpURLConnection conn) throws IOException {
        BufferedReader br;
        if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        return br.lines().collect(Collectors.joining());
    }
}
