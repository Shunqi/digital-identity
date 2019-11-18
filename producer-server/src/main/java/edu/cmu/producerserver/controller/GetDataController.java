package edu.cmu.producerserver.controller;

import edu.cmu.producerserver.model.ConsumerData;
import edu.cmu.producerserver.model.Permission;
import edu.cmu.producerserver.model.PermissionSet;
import edu.cmu.producerserver.repository.ConsumerDataRepository;
import edu.cmu.producerserver.repository.PermissionRepository;
import edu.cmu.producerserver.service.RedisService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/data")
public class GetDataController {

    private final ConsumerDataRepository consumerDataRepository;
    private final RedisService redisClient;
    private final PermissionRepository permissionRepository;

    public GetDataController(ConsumerDataRepository consumerDataRepository, RedisService redisClient,
                             PermissionRepository permissionRepository) {
        this.consumerDataRepository = consumerDataRepository;
        this.redisClient = redisClient;
        this.permissionRepository = permissionRepository;
    }

    @ResponseBody
    @GetMapping("/{category}")
    String getData(@PathVariable String category, @RequestParam(name = "authtoken") String authtoken,
                   HttpServletResponse response) {
        String data = null;

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
        if (permissionSet == null || !permissionSet.isReadable()) {
            response.setStatus(401);
            return "{\"errMsg\": \"Unauthorized\"}";
        }

        ConsumerData consumerData = consumerDataRepository.findByType(category);

        if (consumerData != null) {
            data = consumerData.getData();
        }
        return data;
    }
}
