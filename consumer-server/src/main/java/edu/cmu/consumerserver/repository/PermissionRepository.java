package edu.cmu.consumerserver.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import edu.cmu.consumerserver.model.Permission;

import java.util.List;
public interface PermissionRepository extends MongoRepository<Permission, String> {
    public Permission findByProducerDIDAndConsumerDID(String producerDID, String consumerDID);
}
