package edu.cmu.producerserver.routes;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.json.simple.JSONObject;

@RestController
public class Authentication {

    @RequestMapping(
            value = "/authentication/challenge",
            method = RequestMethod.POST,
            consumes = "text/plain"
    )
    public String challenge(@RequestBody String payload) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(payload);
        System.out.println(json.get("name"));
        return "HEllo World";
    }
}
