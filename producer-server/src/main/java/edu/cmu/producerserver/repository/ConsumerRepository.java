package edu.cmu.producerserver.repository;

import edu.cmu.producerserver.model.Consumer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConsumerRepository extends MongoRepository<Consumer, String> {

    public List<Consumer> findByLastName(String lastName);
}
