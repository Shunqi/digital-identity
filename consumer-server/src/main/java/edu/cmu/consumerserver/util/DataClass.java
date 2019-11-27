package edu.cmu.consumerserver.util;

import java.util.HashMap;
import java.util.Map;

public class DataClass {
    private  String authToken;
    private int count = 0;
    private Map<String,String> data = new HashMap<>();
    private String producerDid;

    public String getConsumerDid(){
        return "2YGJ67123ABC987H";
    }


    public String getProducerDid() {
        return producerDid;
    }

    public void setProducerDid(String producerDid) {
        this.producerDid = producerDid;
    }

    public String getData(String key) {
        return data.get(key);
    }

    public void setData(String key, String value) {
        data.put(key,value);
    }

    public String getProducerHost() {
        return "http://localhost:8082";
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


}
