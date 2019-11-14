package edu.cmu.producerserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "consumerData")
public class ConsumerData {
    @Id
    private String _id;

    private String type;    // health, personal, banking, etc.
    private String data;    // encrypted data

    public ConsumerData(String type, String data) {
        this.type = type;
        this.data = data;
    }

    public String getData() {
        return this.data;
    }
}
