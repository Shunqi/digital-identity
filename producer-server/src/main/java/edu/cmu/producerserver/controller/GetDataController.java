package edu.cmu.producerserver.controller;

import edu.cmu.producerserver.model.ConsumerData;
import edu.cmu.producerserver.repository.ConsumerDataRepository;
import edu.cmu.producerserver.service.RedisService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/data")
public class GetDataController {

    private final ConsumerDataRepository consumerDataRepository;
    private final RedisService redisClient;

    public GetDataController(ConsumerDataRepository consumerDataRepository, RedisService redisClient) {
        this.consumerDataRepository = consumerDataRepository;
        this.redisClient = redisClient;
    }

    @ResponseBody
    @GetMapping("/{type}")
    String getData(@PathVariable String type, @RequestParam(name="authtoken") String authtoken,
                 HttpServletResponse response) {
        String data = null;

        // check authtoken
        String DID = redisClient.getValue(authtoken);
        if (DID == null) {
            response.setStatus(401);
            return null;
        }

        // check permission

        ConsumerData consumerData = consumerDataRepository.findByType(type);

        if (consumerData != null) {
            data = consumerData.getData();
        }
        return data;
    }
}
