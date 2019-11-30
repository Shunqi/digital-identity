package edu.cmu.consumerserver.util;

import java.util.HashMap;
import java.util.Map;

public class DataClass {
    private static String authToken;
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
