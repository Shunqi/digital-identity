package edu.cmu.producerserver.repository;

import edu.cmu.producerserver.model.ProducerData;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ProducerDataRepository extends MongoRepository<ProducerData, String> {
    ProducerData findByType(String type);
}
