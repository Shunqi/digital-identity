package edu.cmu.consumerserver.model;


import org.json.simple.JSONObject;

import java.util.List;

public class PermissionSet {
    private String category;
    private Boolean read;
    private Boolean write;
    private Boolean shareable;
    private List<String> thirdParty;

    @Override
    public String toString() {
        JSONObject data = new JSONObject();
        data.put("category", this.category);
        data.put("read", this.read);
        data.put("write", this.write);
        data.put("shareable", this.shareable);
        data.put("thirdParty", this.thirdParty);

        return data.toJSONString();
    }

    String getCategory() {
        return category;
    }

    public boolean isReadable() {
        return this.read;
    }

    public boolean isWritable() {
        return this.write;
    }

    public  boolean isSharable(){return this.shareable;}

    public List<String> getThirdParty(){return this.thirdParty;}

    public boolean isThirdParty(String did){
        return this.thirdParty.contains(did);
    }


}
