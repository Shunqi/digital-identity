package edu.cmu.consumerserver.routes;

import edu.cmu.consumerserver.security.Security;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

@RestController
public class Authentication {
    private Security security = new Security();
    private String consumerDid = "2YGJ67123ABC987H";

    @RequestMapping(
            value = "/authentication/{did}",
            method = RequestMethod.POST,
            consumes = "text/plain"
    )
    public String challenge(@PathVariable String did) {
        int status;
        try {
            PrivateKey consumerPrivateKey = security.readPrivateKey("src/main/keys/consumer/private.der");
            PublicKey producerPublicKey = security.readPublicKey("src/main/keys/producer/public.der");

            JSONObject challengeMessage = new JSONObject();

            Random random = new Random();
            long challenge = random.nextLong();

            challengeMessage.put("challenge", challenge);
            challengeMessage.put("DID", consumerDid);

            byte[] message = challengeMessage.toJSONString().getBytes(StandardCharsets.UTF_8);
            byte[] secret = security.encrypt(producerPublicKey, message);

            URL url = new URL("http://localhost:8082");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            // write to POST data area
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(new String(secret, StandardCharsets.UTF_8));
            out.close();

            // get HTTP response code sent by server
            status = conn.getResponseCode();
            if (status == 200) {
                String response = conn.getResponseMessage();
                byte[] recovered_message = security.decrypt(consumerPrivateKey, response.getBytes());
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(new String(recovered_message, StandardCharsets.UTF_8));

                if ((long) json.get("challenge") == challenge) {
                    String authtoken = json.get("authtoken").toString();
                    //TO-DO save this to redis
                } else {
                    return null;
                }
            } else {
                return "DID could not be authenticated.";
            }
            conn.disconnect();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "DID is authenticated.";
    }
}