package edu.cmu.producerserver.controller;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/data")
public class GetDataController {

    @ResponseBody
    @GetMapping("/{repoName}")
    String getData(@PathVariable String repoName) {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("name", "John");
        data.put("age", "25");
        result.put(repoName, data);
        return result.toString();
    }
}
