package edu.cmu.consumerserver.routes;

import edu.cmu.consumerserver.util.Connection;
import edu.cmu.consumerserver.util.DataClass;
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
    private Connection connection = new Connection();
    private DataClass dc = new DataClass();

    @RequestMapping(
            value = "/permissions",
            method = RequestMethod.POST,
            consumes = "text/plain"
    )
    public void requestPermission(@RequestBody String permissions, HttpServletResponse response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject dataJSON = (JSONObject) parser.parse(permissions);
        dataJSON.put("consumerDID", dc.getConsumerDid());
        int status;

        try {
            URL url = new URL(dc.getProducerHost() + "/permissions");
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
                response.setContentType("text/plain;charset=UTF-8");
                PrintWriter output = response.getWriter();
                output.println(connection.inputStream(conn));
                output.close();
            } else {
                response.setStatus(401);
            }
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(401);
        }
    }
}
