package edu.cmu.producerserver.model;

import org.json.simple.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "permissions")
public class Permission {

    @Id
    private String _id;

    private String producerDID;
    private String consumerDID;
    private List<PermissionSet> permissions;

    public Permission(String producerDID, String consumerDID, List<PermissionSet> permissions) {
        this.producerDID = producerDID;
        this.consumerDID = consumerDID;
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        JSONObject data = new JSONObject();
        data.put("producerDID", this.producerDID);
        data.put("consumerDID", this.consumerDID);
        data.put("permissions", this.permissions);

        return data.toJSONString();
    }

    public void setPermissions(List<PermissionSet> permissions) {
        this.permissions = permissions;
    }

    public List<PermissionSet> getPermissions() {
        return permissions;
    }

    public PermissionSet getPermission(String category) {
        for (PermissionSet permissionSet : this.permissions) {
            if (permissionSet.getCategory().equals(category)) {
                return permissionSet;
            }
        }
        return null;
    }
}
