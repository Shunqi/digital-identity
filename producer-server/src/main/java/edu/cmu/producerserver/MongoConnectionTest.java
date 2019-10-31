package edu.cmu.producerserver;

import edu.cmu.producerserver.model.Consumer;
import edu.cmu.producerserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class MongoConnectionTest implements CommandLineRunner {

    @Autowired
    private UserRepository repository;

    @Override
    public void run(String... args) throws Exception {
        repository.deleteAll();

        // save a couple of customers
        repository.save(new Consumer("Alice", "Smith", 20));
        repository.save(new Consumer("Bob", "Smith", 21));

        // fetch all customers
        System.out.println("Customers found with findAll():");
        System.out.println("-------------------------------");
        for (Consumer consumer : repository.findAll()) {
            System.out.println(consumer);
        }
        System.out.println();

        System.out.println("Customers found with findByLastName('Smith'):");
        System.out.println("--------------------------------");
        for (Consumer consumer : repository.findByLastName("Smith")) {
            System.out.println(consumer);
        }

    }
}
