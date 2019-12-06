package edu.cmu.producerserver.controller;

import edu.cmu.producerserver.model.ProducerData;
import edu.cmu.producerserver.model.Permission;
import edu.cmu.producerserver.model.PermissionSet;
import edu.cmu.producerserver.repository.ProducerDataRepository;
import edu.cmu.producerserver.repository.PermissionRepository;
import edu.cmu.producerserver.service.RedisService;
import edu.cmu.producerserver.service.Transaction;
import edu.cmu.producerserver.utils.Logger;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/data")
public class DataRetrievalController {

    private final ProducerDataRepository producerDataRepository;
    private final RedisService redisClient;
    private final PermissionRepository permissionRepository;
    private final Logger logger;
    private Transaction transaction;

    public DataRetrievalController(ProducerDataRepository producerDataRepository, RedisService redisClient,
                                   PermissionRepository permissionRepository, Logger logger) throws Exception {
        this.producerDataRepository = producerDataRepository;
        this.redisClient = redisClient;
        this.permissionRepository = permissionRepository;
        this.logger = logger;
        transaction = new Transaction();
    }

    // RSA private key for Producer
    private static final String pn = "23125112666426093876263740909764910339347312551313945559240958270498185928023218019" +
            "0106921111613716541064347138357563940947734110924610394534466816125228346183846412686151923456654254591206" +
            "2460393823188503721918025583888570206681611568376689";
    private static final String pd = "12300554305989191335071089737314872124595988762665427807118723855372793406028493524" +
            "92046823924020653771381531233218590867874292706821045946917359993739030953185269621984367826097994320512372" +
            "66851512692162683998375202714562516423826155184313";
    private static final String producerDID = "T3CS82NLOD9KIW8X";

    @ResponseBody
    @GetMapping("/{category}")
    String getData(@PathVariable String category, @RequestParam(name = "authtoken") String authtoken,
                   @RequestParam(name = "thirdpartydid") String thirdPartyDid,
                   HttpServletResponse response, HttpServletRequest request) throws Exception {
        String data = null;

        String logType;
        String extraMsg;
        if (thirdPartyDid == null) {
            logType = "Data Retrieval";
            extraMsg = "";
        } else {
            logType = "Third Party Data Retrieval";
            extraMsg = " From third party: " + thirdPartyDid;
        }

        // check authtoken
        String consumerDID = redisClient.getValue(authtoken);
        if (consumerDID == null) {
            response.setStatus(401);
            logger.log(null, logType, request.getRequestURI(), "Rejected", "Invalid authtoken" + extraMsg);
            return "{\"errMsg\": \"Invalid authtoken\"}";
        }

        // check permission
        Permission permission = permissionRepository.findByConsumerDID(consumerDID);
        if (permission == null) {
            response.setStatus(401);
            logger.log(consumerDID, logType, request.getRequestURI(), "Rejected", "Unauthorized" + extraMsg);
            return "{\"errMsg\": \"Unauthorized\"}";
        }

        // check permission set
        PermissionSet permissionSet = permission.getPermission(category);
        if (permissionSet == null || !permissionSet.isReadable()) {
            response.setStatus(401);
            logger.log(consumerDID, logType, request.getRequestURI(), "Rejected", "Unauthorized" + extraMsg);
            return "{\"errMsg\": \"Unauthorized\"}";
        }

        ProducerData producerData = producerDataRepository.findByType(category);
        if (producerData != null) {
            data = producerData.getData();
        }


        JSONObject result = new JSONObject();
        if (data != null) {
            BigInteger decryptedData = decrypt(new BigInteger(data));
            BigInteger secureToken = encrypt(decryptedData.toByteArray());

            JSONObject dataJSON = new JSONObject(new String(decryptedData.toByteArray()));
            result.put("secureToken", secureToken);
            result.put("data", dataJSON);
        }

        logger.log(consumerDID, logType, request.getRequestURI(), "Accepted", "Data retrieved." + extraMsg);
        return result.toString();
    }

    @ResponseBody
    @PostMapping("/{category}")
    String writeData(@PathVariable String category, @RequestBody Map<String, Object> payload,
                     HttpServletResponse response) throws Exception {
        String authtoken = (String) payload.getOrDefault("authtoken", null);
        Object payloadData = payload.getOrDefault("data", null);
        if (authtoken == null || payloadData == null) {
            response.setStatus(401);
            return "{\"errMsg\": \"Missing required field\"}";
        }

//        String data = payloadData.toString();
        JSONObject data = new JSONObject((LinkedHashMap) payloadData);

        // check authtoken
        String consumerDID = redisClient.getValue(authtoken);
        if (consumerDID == null) {
            response.setStatus(401);
            return "{\"errMsg\": \"Invalid authtoken\"}";
        }

        // check permission
        Permission permission = permissionRepository.findByConsumerDID(consumerDID);
        if (permission == null) {
            response.setStatus(401);
            return "{\"errMsg\": \"Unauthorized\"}";
        }

        // check permission set
        PermissionSet permissionSet = permission.getPermission(category);
        if (permissionSet == null || !permissionSet.isWritable()) {
            response.setStatus(401);
            return "{\"errMsg\": \"Unauthorized\"}";
        }

        BigInteger encryptedData = encrypt(data.toString().getBytes());

        ProducerData producerData = producerDataRepository.findByType(category);
        if (producerData == null) {
            // create new data
            producerData = new ProducerData(category, encryptedData.toString());
        } else {
            // update data
            producerData.setData(encryptedData.toString());
        }
        producerDataRepository.save(producerData);

        return "{\"status\": \"Success\"}";
    }

    private BigInteger encrypt(byte[] secretBytes) throws Exception {
        String consumerKeys = transaction.getKey(producerDID);
        String ce = consumerKeys.split(",")[0];
        String cn = consumerKeys.split(",")[1];
        return encryptData(secretBytes, ce, cn);
    }

    private BigInteger decrypt(BigInteger payload){
        return decryptData(payload, pd, pn);
    }

    private BigInteger encryptData(byte[] data, String e, String n) {
        BigInteger m = new BigInteger(data);
        return m.modPow(new BigInteger(e), new BigInteger(n));
    }

    private BigInteger decryptData(BigInteger data, String ed, String n) {
        return data.modPow(new BigInteger(ed), new BigInteger(n));
    }
}
