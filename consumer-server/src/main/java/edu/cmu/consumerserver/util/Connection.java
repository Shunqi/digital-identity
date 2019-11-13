package edu.cmu.consumerserver.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

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
}
