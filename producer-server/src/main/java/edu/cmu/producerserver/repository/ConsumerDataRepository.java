package edu.cmu.producerserver.repository;

import edu.cmu.producerserver.model.ConsumerData;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ConsumerDataRepository extends MongoRepository<ConsumerData, String> {
    ConsumerData findByType(String type);
}
