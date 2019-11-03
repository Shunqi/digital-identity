package edu.cmu.consumerserver.routes;

import edu.cmu.consumerserver.security.AsymmetricKey;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

@RestController
public class Authentication {
    private AsymmetricKey asymmetricKey = new AsymmetricKey();
    private String consumerDid = "2YGJ67123ABC987H";
    private String producerHost = "http://localhost:8082";

    @RequestMapping(
            value = "/authentication/key",
            method = RequestMethod.GET,
            consumes = "text/plain"
    )
    public void setUpSymmetricKey(HttpServletResponse response) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException,
            IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        StringBuilder result = new StringBuilder();
        URL url = new URL(producerHost + "/authentication/key");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
        conn.setRequestProperty("Accept-Charset", "UTF-8");
        conn.setRequestMethod("GET");

        PrivateKey privateKey = asymmetricKey.readPrivateKey("src/main/keys/consumer/private.der");
        byte[] secret = asymmetricKey.decrypt(privateKey, getByteArray(conn));
        System.out.println(new String(secret, StandardCharsets.UTF_8));

        response.setStatus(200);
    }

    @RequestMapping(
            value = "/authentication/did",
            method = RequestMethod.POST,
            consumes = "text/plain",
            produces = MediaType.TEXT_HTML_VALUE
    )
    public String challenge(@RequestBody String did) {
        int status;
        try {
            PrivateKey consumerPrivateKey = asymmetricKey.readPrivateKey("src/main/keys/consumer/private.der");
            PublicKey producerPublicKey = asymmetricKey.readPublicKey("src/main/keys/producer/public.der");

            JSONObject challengeMessage = new JSONObject();

            Random random = new Random();
            long challenge = random.nextLong();

            challengeMessage.put("challenge", challenge);
            challengeMessage.put("DID", consumerDid);

            byte[] message = challengeMessage.toJSONString().getBytes(StandardCharsets.UTF_8);
            byte[] secret = asymmetricKey.encrypt(producerPublicKey, message);

            URL url = new URL(producerHost + "/authentication/challenge");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setDoOutput(true);
            // write to POST data area
            OutputStream out = conn.getOutputStream();
            out.write(secret);
            out.close();

            // get HTTP response code sent by server
            status = conn.getResponseCode();
            if (status == 200) {
                byte[] recovered_message = asymmetricKey.decrypt(consumerPrivateKey, getByteArray(conn));
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(new String(recovered_message, StandardCharsets.UTF_8));

                if ((long) json.get("challenge") == (challenge + 1)) {
                    String authToken = json.get("authToken").toString();
                    System.out.println("Challenge complete");
                } else {
                    return "<div><h3>DID could not be authenticated.</h3></div>";
                }
            } else {
                return "<div><h3>DID could not be authenticated.</h3></div>";
            }
            conn.disconnect();
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | ParseException | BadPaddingException |
                NoSuchPaddingException | IllegalBlockSizeException | IOException e) {
            e.printStackTrace();
            return "<div><h3>DID could not be authenticated.</h3></div>";
        }
//        return "DID is authenticated.";
        return "<div><h3>DID is authenticated.</h3></div>";
    }

    private byte[] getByteArray(HttpURLConnection conn) throws IOException {
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