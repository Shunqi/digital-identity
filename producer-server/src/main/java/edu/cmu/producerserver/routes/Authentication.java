package edu.cmu.producerserver.routes;

import edu.cmu.producerserver.security.Security;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.PrivateKey;
import java.security.PublicKey;

@RestController
public class Authentication {

    String appURL = "http://10.0.0.153:8081";
    Security sec = new Security();

    @RequestMapping(
            value = "/authentication/challenge",
            method = RequestMethod.POST,
            consumes = "text/plain"
    )
    public String challenge(@RequestBody String payload, HttpServletResponse response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(payload);

        // Decrypt the string received and get the challenge value

        // Make call to the app
        try {
            URL url = new URL(appURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if(conn.getResponseCode() == 200) {
                // Generate auth token and store in redis
                //cresponse.getWriter().write(json.toJSONString());
            } else if(conn.getResponseCode() == 401) {
                // Send back saying unauthorized
            }

            PublicKey publicKey = sec.readPublicKey("src/main/keys/public.der");
            PrivateKey privateKey = sec.readPrivateKey("src/main/keys/private.der");
            byte[] message = "Hello World from ".getBytes("UTF8");
            byte[] secret = sec.encrypt(publicKey, message);
            byte[] recovered_message = sec.decrypt(privateKey, secret);
            System.out.println(new String(recovered_message, "UTF8"));

            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(json.get("name"));
        return "HEllo World";
    }
}
