package edu.cmu.producerserver.model;

import org.json.simple.JSONObject;

public class PermissionSet {
    private String category;
    private Boolean read;
    private Boolean write;
    private Boolean shareable;
    private String thirdPartyDIDs;

    @Override
    public String toString() {
        JSONObject data = new JSONObject();
        data.put("category", this.category);
        data.put("read", this.read);
        data.put("write", this.write);
        data.put("shareable", this.shareable);
        data.put("thirdPartyDIDs", this.thirdPartyDIDs);

        return data.toJSONString();
    }

    public String getCategory() {
        return category;
    }

    public boolean isReadable() {
        return this.read;
    }

    public boolean isWritable() {
        return this.write;
    }
}
