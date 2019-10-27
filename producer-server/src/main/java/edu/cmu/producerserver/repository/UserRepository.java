package edu.cmu.producerserver.repository;

import edu.cmu.producerserver.model.Consumer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<Consumer, String> {

    public List<Consumer> findByLastName(String lastName);
}
