package edu.cmu.producerserver.repository;

import edu.cmu.producerserver.model.Log;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogRepository extends MongoRepository<Log, String> {
//    Log findBy_id(ObjectId _id);
}
