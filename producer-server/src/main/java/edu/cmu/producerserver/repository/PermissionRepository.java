package edu.cmu.producerserver.repository;

import edu.cmu.producerserver.model.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PermissionRepository extends MongoRepository<Permission, String> {
    public Permission findByConsumerDID(String consumerDID);
//    public void updatePermissions(String consumerDID, List<Permission> permissionSet);
}
