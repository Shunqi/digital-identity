package edu.cmu.producerserver.routes;

import edu.cmu.producerserver.pushnotifications.CcsClient;
import edu.cmu.producerserver.pushnotifications.MessageHelper;
import edu.cmu.producerserver.pushnotifications.bean.CcsOutMessage;
import edu.cmu.producerserver.security.AsymmetricKey;
import edu.cmu.producerserver.security.SymmetricKey;
import edu.cmu.producerserver.pushnotifications.util.*;

import edu.cmu.producerserver.service.RedisService;
import edu.cmu.producerserver.service.Transaction;
import edu.cmu.producerserver.utils.Hashing;
import edu.cmu.producerserver.utils.Logger;
import edu.cmu.producerserver.utils.MessagingCredentials;
import org.apache.commons.lang3.RandomStringUtils;
import org.jivesoftware.smack.XMPPException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Authentication {
    private static final String producerDID = "T3CS82NLOD9KIW8X";
    private static final String consumerDID = "2YGJ67123ABC987H";

    // RSA private key for Producer
    private static final String pn = "23125112666426093876263740909764910339347312551313945559240958270498185928023218019" +
            "0106921111613716541064347138357563940947734110924610394534466816125228346183846412686151923456654254591206" +
            "2460393823188503721918025583888570206681611568376689";
    private static final String pd = "12300554305989191335071089737314872124595988762665427807118723855372793406028493524" +
            "92046823924020653771381531233218590867874292706821045946917359993739030953185269621984367826097994320512372" +
            "66851512692162683998375202714562516423826155184313";

    AsymmetricKey asymmetricKey = new AsymmetricKey();
    SymmetricKey symmetricKey = new SymmetricKey();
    Hashing hash = new Hashing();
    Transaction transaction = new Transaction();
    MessagingCredentials credentials = new MessagingCredentials();

    @Autowired
    private Logger logger;

    private final RedisService redisClient;

    public Authentication(RedisService redisClient) throws Exception {
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
    public void challenge(@RequestBody byte[] payload, HttpServletResponse response) throws ParseException, IOException {
        JSONParser parser = new JSONParser();
//        PrivateKey privateKey = asymmetricKey.readPrivateKey("src/main/keys/producer/private.der");
//        byte[] secret = asymmetricKey.decrypt(privateKey, payload);
//        String data = new String(secret, "UTF8");
        BigInteger data = decryptData(new BigInteger(payload), pd, pn);
        System.out.println(new String(data.toByteArray(), "UTF8"));

        JSONObject dataJSON = (JSONObject) parser.parse(new String(data.toByteArray(), "UTF8"));
        long challenge = (long) dataJSON.get("challenge");
        String DID = (String) dataJSON.get("DID");

        // Make call to the app
        try {
            CcsClient ccsClient = CcsClient.prepareClient(credentials.projectID, credentials.apiKey, true);

            try {
                ccsClient.connect();
            } catch (XMPPException e) {
                System.out.println("System error" + e);
            }

            // Send a sample downstream message to a device
            String messageId = Util.getUniqueMessageId();
            Map<String, String> dataPayload = new HashMap<String, String>();
            dataPayload.put(Util.PAYLOAD_ATTRIBUTE_MESSAGE, "Authentication");
            CcsOutMessage message = new CcsOutMessage(credentials.senderRegistrationID, messageId, dataPayload);
            String jsonRequest = MessageHelper.createJsonOutMessage(message);
            ccsClient.send(jsonRequest);

            while(true) {
                if(ccsClient.set) {
                    if(ccsClient.appAuthenticationResponse.equals("YES")) {
                        String consumerKeys = transaction.getKey(consumerDID);
                        String ce = consumerKeys.split(",")[0];
                        String cn = consumerKeys.split(",")[1];
                        String authToken = hash.getHash(DID);

                        JSONObject challengeResponse = new JSONObject();
                        challengeResponse.put("challenge", challenge + 1);
                        challengeResponse.put("authToken", authToken);

                        redisClient.put(authToken, DID);
                        redisClient.setTTL(authToken, 30);

                        byte[] secretBytes = challengeResponse.toJSONString().getBytes(StandardCharsets.UTF_8);
                        BigInteger secret = encryptData(secretBytes, ce, cn);

//                        PublicKey consumerPublicKey = asymmetricKey.readPublicKey("src/main/keys/consumer/public.der");
//                        byte[] challengeResponseBytes = asymmetricKey.encrypt(consumerPublicKey, challengeResponse.toJSONString().getBytes(StandardCharsets.UTF_8));

                        logger.log(DID,"Authentication", "/authentication/challenge", "Accepted", "DID authenticated");

                        ccsClient.set = false;
                        response.setStatus(200);
                        OutputStream out = response.getOutputStream();
                        out.write(secret.toByteArray());
                        out.close();
                        break;
                    } else {
                        logger.log(DID,"Authentication", "/authentication/challenge", "Rejected", "DID not authenticated");
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

    private BigInteger encryptData(byte[] data, String e, String n) {
        BigInteger m = new BigInteger(data);
        BigInteger c = m.modPow(new BigInteger(e), new BigInteger(n));
        return c;
    }

    private BigInteger decryptData(BigInteger data, String ed, String n) {
        BigInteger decryptedData = data.modPow(new BigInteger(ed), new BigInteger(n));
        return decryptedData;
    }
}
