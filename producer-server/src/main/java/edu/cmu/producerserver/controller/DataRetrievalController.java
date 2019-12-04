package edu.cmu.producerserver.controller;

import edu.cmu.producerserver.model.ConsumerData;
import edu.cmu.producerserver.model.Permission;
import edu.cmu.producerserver.model.PermissionSet;
import edu.cmu.producerserver.repository.ConsumerDataRepository;
import edu.cmu.producerserver.repository.PermissionRepository;
import edu.cmu.producerserver.security.AsymmetricKey;
import edu.cmu.producerserver.service.RedisService;
import edu.cmu.producerserver.utils.Logger;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/data")
public class DataRetrievalController {

    private final ConsumerDataRepository consumerDataRepository;
    private final RedisService redisClient;
    private final PermissionRepository permissionRepository;
    private static AsymmetricKey asymmetricKey;
    private final Logger logger;

    public DataRetrievalController(ConsumerDataRepository consumerDataRepository, RedisService redisClient,
                                   PermissionRepository permissionRepository, Logger logger) {
        this.consumerDataRepository = consumerDataRepository;
        this.redisClient = redisClient;
        this.permissionRepository = permissionRepository;
        asymmetricKey = new AsymmetricKey();
        this.logger = logger;
    }

    @ResponseBody
    @GetMapping("/{category}")
    String getData(@PathVariable String category, @RequestParam(name = "authtoken") String authtoken,
                   @RequestParam(name = "thirdpartydid") String thirdPartyDid,
                   HttpServletResponse response, HttpServletRequest request) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        String data = null;

        String logType;
        if (thirdPartyDid == null) {
            logType = "Data Retrieval";
        } else {
            logType = "Third Party Data Retrieval";
        }

        // check authtoken
        String consumerDID = redisClient.getValue(authtoken);
        if (consumerDID == null) {
            response.setStatus(401);
            logger.log(null, logType, request.getRequestURI(), "Rejected", "Invalid authtoken");
            return "{\"errMsg\": \"Invalid authtoken\"}";
        }

        // check permission
        Permission permission = permissionRepository.findByConsumerDID(consumerDID);
        if (permission == null) {
            response.setStatus(401);
            logger.log(consumerDID, logType, request.getRequestURI(), "Rejected", "Unauthorized");
            return "{\"errMsg\": \"Unauthorized\"}";
        }

        // check permission set
        PermissionSet permissionSet = permission.getPermission(category);
        if (permissionSet == null || !permissionSet.isReadable()) {
            response.setStatus(401);
            logger.log(consumerDID, logType, request.getRequestURI(), "Rejected", "Unauthorized");
            return "{\"errMsg\": \"Unauthorized\"}";
        }

        ConsumerData consumerData = consumerDataRepository.findByType(category);
        if (consumerData != null) {
            data = consumerData.getData();
        }

        // TODO: add secure token
        if (data != null) {
            PrivateKey privateKey = asymmetricKey.readPrivateKey("src/main/keys/producer/private.der");
            byte[] secret = asymmetricKey.decrypt(privateKey, Base64.getDecoder().decode(data.getBytes()));
            data = new String(secret, StandardCharsets.UTF_8);
        }

        logger.log(consumerDID, logType, request.getRequestURI(), "Accepted", "Data retrieved.");
        return data;
    }

    @ResponseBody
    @PostMapping("/{category}")
    String writeData(@PathVariable String category, @RequestBody Map<String, Object> payload,
                     HttpServletResponse response) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        String authtoken = (String) payload.getOrDefault("authtoken", null);
        Object payloadData = payload.getOrDefault("data", null);
        if (authtoken == null || payloadData == null) {
            response.setStatus(401);
            return "{\"errMsg\": \"Missing required field\"}";
        }

        String data = payloadData.toString();

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

        PublicKey publicKey = asymmetricKey.readPublicKey("src/main/keys/producer/public.der");
        System.out.println(data);
        byte[] secret = asymmetricKey.encrypt(publicKey, data.getBytes(StandardCharsets.UTF_8));
        String encryptedData = Base64.getEncoder().encodeToString(secret);

        ConsumerData consumerData = consumerDataRepository.findByType(category);
        if (consumerData == null) {
            // create new data
            consumerData = new ConsumerData(category, encryptedData);
        } else {
            // update data
            consumerData.setData(encryptedData);
        }
        consumerDataRepository.save(consumerData);

        return "{\"status\": \"Success\"}";
    }
}
