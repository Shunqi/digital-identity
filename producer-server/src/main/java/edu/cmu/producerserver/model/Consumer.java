package edu.cmu.producerserver.model;

import org.springframework.data.annotation.Id;

public class Consumer {

    @Id
    private String _id;

    private String firstName;
    private String lastName;
    private int age;

    public Consumer(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    @Override
    public String toString() {
        return String.format(
                "Consumer[id=%s, name='%s', age='%s']",
                _id, firstName + " " + lastName, age);
    }
}

