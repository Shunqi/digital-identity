package edu.cmu.producerserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "producerData")
public class ProducerData {
    @Id
    private String _id;

    private String type;    // health, personal, banking, etc.
    private String data;    // encrypted data

    public ProducerData(String type, String data) {
        this.type = type;
        this.data = data;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
