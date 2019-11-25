package edu.cmu.consumerserver.routes;

import edu.cmu.consumerserver.security.*;
import edu.cmu.consumerserver.util.DataClass;
import edu.cmu.consumerserver.service.Transaction;
import edu.cmu.consumerserver.util.Connection;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

@RestController
public class Authentication {
    private AsymmetricKey asymmetricKey = new AsymmetricKey();
    Transaction transaction = new Transaction();
    private Connection connection = new Connection();
    private DataClass dc = new DataClass();

    // RSA private keys for Consumer
    private static final String cn = "298870257550107893751445046581463981771828158714859729887101012502967312191860154" +
            "5823749993119352654841219269943974793479059359333606477792042853866442333375176798349941284963733204839659" +
            "682372671091398391982199850782879099787757261164600919";
    private static final String cd = "161002402349917744005596340533722719630962557691504383228773078511638630955546069" +
            "5108221211027034278028735619960816486620622466164311423768436711539991114753330781216270480746629133701930" +
            "886217933084946833841872533182100453470929548348822473";

    public Authentication() throws Exception {
    }

    @RequestMapping(
            value = "/authentication/key",
            method = RequestMethod.GET,
            consumes = "text/plain"
    )
    public void setUpSymmetricKey(HttpServletResponse response) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException,
            IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        StringBuilder result = new StringBuilder();
        URL url = new URL(dc.getProducerHost() + "/authentication/key");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
        conn.setRequestProperty("Accept-Charset", "UTF-8");
        conn.setRequestMethod("GET");

        PrivateKey privateKey = asymmetricKey.readPrivateKey("src/main/keys/consumer/private.der");
        byte[] secret = asymmetricKey.decrypt(privateKey, connection.getByteArray(conn));
        System.out.println(new String(secret, StandardCharsets.UTF_8));

        response.setStatus(200);
    }

    @RequestMapping(
            value = "/authentication/did",
            method = RequestMethod.POST,
            consumes = "text/plain",
            produces = MediaType.TEXT_HTML_VALUE
    )
    public void challenge(@RequestBody String did, HttpServletResponse response, HttpServletRequest request) throws ServletException, IOException {
        int status;
        dc.setProducerDid(did);
        boolean serverResponse = false;
        try {
            String producerKeys = transaction.getKey(did);
            String pe = producerKeys.split(",")[0];
            String pn = producerKeys.split(",")[1];
//            PrivateKey consumerPrivateKey = asymmetricKey.readPrivateKey("src/main/keys/consumer/private.der");
//            PublicKey producerPublicKey = asymmetricKey.readPublicKey("src/main/keys/producer/public.der");

            JSONObject challengeMessage = new JSONObject();

            Random random = new Random();
            long challenge = random.nextLong();

            challengeMessage.put("challenge", challenge);
            challengeMessage.put("DID", dc.getConsumerDid());

            byte[] message = challengeMessage.toJSONString().getBytes(StandardCharsets.UTF_8);
            BigInteger secret = encryptData(message, pe, pn);

            URL url = new URL(dc.getProducerHost() + "/authentication/challenge");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setDoOutput(true);
            // write to POST data area
            OutputStream out = conn.getOutputStream();
            out.write(secret.toByteArray());
            out.close();

            // get HTTP response code sent by server
            status = conn.getResponseCode();
            if (status == 200) {
//                byte[] recovered_message = asymmetricKey.decrypt(consumerPrivateKey, connection.getByteArray(conn));
                BigInteger recovered_message = decryptData(new BigInteger(connection.getByteArray(conn)), cd, cn);
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(new String(recovered_message.toByteArray(), StandardCharsets.UTF_8));

                if ((long) json.get("challenge") == (challenge + 1)) {
                    serverResponse = true;
                    dc.setAuthToken(json.get("authToken").toString());
                }
            }
            conn.disconnect();
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | ParseException | BadPaddingException |
                NoSuchPaddingException | IllegalBlockSizeException | IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (serverResponse)
            response.setStatus(200);
        else
            response.setStatus(401);
    }

    private BigInteger encryptData(byte[] data, String e, String n) {
        BigInteger m = new BigInteger(data);
        BigInteger c = m.modPow(new BigInteger(e), new BigInteger(n));
        return c;
    }

    private BigInteger decryptData(BigInteger data, String d, String n) {
        BigInteger decryptedData = data.modPow(new BigInteger(d), new BigInteger(n));
        return decryptedData;
    }
}