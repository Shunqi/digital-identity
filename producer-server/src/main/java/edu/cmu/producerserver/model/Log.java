package edu.cmu.producerserver.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.simple.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Document(collection = "logs")
public class Log {

    @Id
    private String _id;

    @DateTimeFormat(iso = ISO.DATE_TIME)
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
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        return strDate;
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