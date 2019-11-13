package edu.cmu.consumerserver.routes;

import edu.cmu.consumerserver.util.Connection;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
public class Permissions {
    private String producerHost = "http://localhost:8082";
    private Connection connection = new Connection();
    private String consumerDID = "2YGJ67123ABC987H";

    @RequestMapping(
            value = "/permissions",
            method = RequestMethod.POST,
            consumes = "text/plain"
    )
    public void requestPermission(@RequestBody String permissions, HttpServletResponse response) throws ParseException {
        System.out.println("Hello");
        JSONParser parser = new JSONParser();
        JSONObject dataJSON = (JSONObject) parser.parse(permissions);
        dataJSON.put("consumerDID", consumerDID);
        int status;

        try {
            URL url = new URL(producerHost + "/permissions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setDoOutput(true);
            // write to POST data area
            OutputStream out = conn.getOutputStream();
            out.write(dataJSON.toJSONString().getBytes());
            out.close();

            status = conn.getResponseCode();
            if (status == 200) {
                response.setStatus(200);
//                response.setContentType("text/plain;charset=UTF-8");
//                PrintWriter output = response.getWriter();
//                output.println(new String(connection.getByteArray(conn)));
//                output.close();
            } else {
                response.setStatus(401);
//                response.setContentType("text/plain;charset=UTF-8");
//                PrintWriter output = response.getWriter();
//                output.println("Permission denied.");
//                output.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
