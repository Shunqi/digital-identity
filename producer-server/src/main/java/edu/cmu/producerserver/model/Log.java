package edu.cmu.producerserver.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "logs")
public class Log {

    @Id
    private String _id;

    private String timestamp;
    private String consumerDID;
    private String type;
    private String route;
    private String status;
    private String message;

    public Log() {

    }

    public Log(String consumerDID, String type, String route, String status, String message) {
        this.timestamp = getDate();
        this.consumerDID = consumerDID;
        this.type = type;
        this.route = route;
        this.status = status;
        this.message = message;
    }

    public String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

    @Override
    public String toString() {
        JSONObject data = new JSONObject();
        data.put("timestamp", this.timestamp);
        data.put("DID", this.consumerDID);
        data.put("type", this.type);
        data.put("route", this.route);
        data.put("status", this.status);
        data.put("message", this.message);

        return data.toJSONString();
    }
}