package edu.cmu.producerserver.controller;

import edu.cmu.producerserver.model.ConsumerData;
import edu.cmu.producerserver.repository.ConsumerDataRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/data")
public class GetDataController {

    private final ConsumerDataRepository consumerDataRepository;

    public GetDataController(ConsumerDataRepository consumerDataRepository) {
        this.consumerDataRepository = consumerDataRepository;
    }

    @ResponseBody
    @GetMapping("/{type}")
    String getData(@PathVariable String type) {
        ConsumerData consumerData = consumerDataRepository.findByType(type);

        String result = null;
        if (consumerData != null) {
            result = consumerData.getData();
        }

        return result;
    }
}
