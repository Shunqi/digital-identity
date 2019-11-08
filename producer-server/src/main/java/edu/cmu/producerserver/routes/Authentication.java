package edu.cmu.producerserver.routes;

import edu.cmu.producerserver.pushnotifications.CcsClient;
import edu.cmu.producerserver.pushnotifications.MessageHelper;
import edu.cmu.producerserver.pushnotifications.bean.CcsOutMessage;
import edu.cmu.producerserver.security.AsymmetricKey;
import edu.cmu.producerserver.security.SymmetricKey;
import edu.cmu.producerserver.pushnotifications.util.*;

import edu.cmu.producerserver.service.RedisTestService;
import edu.cmu.producerserver.utils.Hashing;
import org.apache.commons.lang3.RandomStringUtils;
import org.jivesoftware.smack.XMPPException;
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
import java.util.HashMap;
import java.util.Map;

@RestController
public class Authentication {

    private static final long serialVersionUID = -8022560668279505764L;

    // Method to send Notifications from server to client end.
    public final static String AUTH_KEY_FCM = "AAAA1yG1bXs:APA91bGTxsybnU8wiWzxzXuNgRpeYNjGred7PJIZRFaQJcqgzrFfQGA0jESW7c1Wo298KR3gor5lzMkam6uEJzb6QCHzw-GDWCIGcscu3XvNkTO5agE2QPTUrU9OM8EG8hqD33R7qCvJ";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/v1/projects/DIDPushNotifications/messages:send";
    public final static String DEVICE_ID = "dHdgar30H_s:APA91bEjZA7OUNj98zinwq3Dh8gWualDjacfbEte4NaS8y59inXzLx-By30CagZIoym2NZ4kv9S2yvycmpMMHJUk0hkP3QsKiZ2eU8_3O4fO2zF_szduRj11jPOEwHpLpheHOYg9scOr";

    AsymmetricKey asymmetricKey = new AsymmetricKey();
    SymmetricKey symmetricKey = new SymmetricKey();
    Hashing hash = new Hashing();

    private final RedisTestService redisClient;

    public Authentication(RedisTestService redisClient) {
        this.redisClient = redisClient;
    }

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
        String DID = (String) dataJSON.get("DID");

        // Make call to the app
        try {
            CcsClient ccsClient = CcsClient.prepareClient("923983506811", "AAAA1yG1bXs:APA91bGTxsyb" +
                    "nU8wiWzxzXuNgRpeYNjGred7PJIZRFaQJcqgzrFfQGA0jESW7c1Wo298KR3gor5lzMkam6uEJzb6QCHzw-GDWCIGcscu3XvNkTO5agE2QPTUrU9OM8EG8hqD33R7qCvJ", true);

            try {
                ccsClient.connect();
            } catch (XMPPException e) {
                System.out.println("System error" + e);
            }

            // Send a sample downstream message to a device
            String messageId = Util.getUniqueMessageId();
            Map<String, String> dataPayload = new HashMap<String, String>();
            dataPayload.put(Util.PAYLOAD_ATTRIBUTE_MESSAGE, "Authentication");
            CcsOutMessage message = new CcsOutMessage("dHdgar30H_s:APA91bEjZA7OUNj98zinwq3Dh8gWualDjacfbEte4NaS8y59inXzL" +
                    "x-By30CagZIoym2NZ4kv9S2yvycmpMMHJUk0hkP3QsKiZ2eU8_3O4fO2zF_szduRj11jPOEwHpLpheHOYg9scOr", messageId, dataPayload);
            String jsonRequest = MessageHelper.createJsonOutMessage(message);
            ccsClient.send(jsonRequest);

            while(true) {
                if(ccsClient.set) {
                    if(ccsClient.appAuthenticationResponse.equals("YES")) {
                        String authToken = hash.getHash(DID);

                        JSONObject challengeResponse = new JSONObject();
                        challengeResponse.put("challenge", challenge + 1);
                        challengeResponse.put("authToken", authToken);

                        redisClient.put(authToken, DID);
                        redisClient.setTTL(authToken, 30);

                        PublicKey consumerPublicKey = asymmetricKey.readPublicKey("src/main/keys/consumer/public.der");
                        byte[] challengeResponseBytes = asymmetricKey.encrypt(consumerPublicKey, challengeResponse.toJSONString().getBytes(StandardCharsets.UTF_8));

                        ccsClient.set = false;
                        response.setStatus(200);
                        OutputStream out = response.getOutputStream();
                        out.write(challengeResponseBytes);
                        out.close();
                        break;
                    } else {
                        response.setStatus(401);
                        ccsClient.set = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
