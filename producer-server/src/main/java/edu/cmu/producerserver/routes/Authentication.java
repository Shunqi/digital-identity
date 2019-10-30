package edu.cmu.producerserver.routes;

import edu.cmu.producerserver.security.AsymmetricKey;
import edu.cmu.producerserver.security.SymmetricKey;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.json.simple.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

@RestController
public class Authentication {

    String appURL = "http://10.0.0.153:8081";
    AsymmetricKey asymmetricKey = new AsymmetricKey();
    SymmetricKey symmetricKey = new SymmetricKey();

    @RequestMapping(
            value = "/authentication/key",
            method = RequestMethod.GET,
            consumes = "text/plain"
    )
    public void establishKey(HttpServletResponse response) throws NoSuchPaddingException, IOException, NoSuchAlgorithmException,
            InvalidKeySpecException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        String secretKey = RandomStringUtils.randomAlphabetic(5);
        symmetricKey.setKey(secretKey, 15, "AES");
        PublicKey publicKey = asymmetricKey.readPublicKey("src/main/keys/consumer/public.der");
        byte[] secret = asymmetricKey.encrypt(publicKey, secretKey.getBytes("UTF8"));
        response.setStatus(200);
        response.setContentType("text/plain; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        OutputStream out = response.getOutputStream();
        out.write(secret);
        out.close();
        out.flush();
    }

    @RequestMapping(
            value = "/authentication/challenge",
            method = RequestMethod.POST,
            consumes = "text/plain"
    )
    public void challenge(@RequestBody byte[] payload, HttpServletResponse response) throws ParseException, IOException,
            NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, NoSuchPaddingException {
        JSONParser parser = new JSONParser();
        PrivateKey privateKey = asymmetricKey.readPrivateKey("src/main/keys/producer/private.der");

        byte[] secret = asymmetricKey.decrypt(privateKey, payload);
        String data = new String(secret, "UTF8");

        JSONObject dataJSON = (JSONObject) parser.parse(data);
        long challenge = (long) dataJSON.get("challenge");

        // Make call to the app
        try {
//            URL url = new URL(appURL);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Accept", "application/json");
//
//            if(conn.getResponseCode() == 200) {
//                // Generate auth token and store in redis
//                //cresponse.getWriter().write(json.toJSONString());
//            } else if(conn.getResponseCode() == 401) {
//                // Send back saying unauthorized
//            }

            JSONObject challengeResponse = new JSONObject();
            challengeResponse.put("challenge", challenge + 1);
            challengeResponse.put("authToken", "eqweqwe");

            PublicKey consumerPublicKey = asymmetricKey.readPublicKey("src/main/keys/consumer/public.der");
            byte[] challengeResponseBytes = asymmetricKey.encrypt(consumerPublicKey, challengeResponse.toJSONString().getBytes(StandardCharsets.UTF_8));

            response.setStatus(200);
            OutputStream out = response.getOutputStream();
            out.write(challengeResponseBytes);
            out.close();
//            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
