package edu.cmu.producerserver.routes;

import edu.cmu.producerserver.model.Permission;
import edu.cmu.producerserver.model.PermissionSet;
import edu.cmu.producerserver.pushnotifications.CcsClient;
import edu.cmu.producerserver.pushnotifications.MessageHelper;
import edu.cmu.producerserver.pushnotifications.bean.CcsOutMessage;
import edu.cmu.producerserver.pushnotifications.util.Util;
import edu.cmu.producerserver.repository.LogRepository;
import edu.cmu.producerserver.repository.PermissionRepository;
import edu.cmu.producerserver.utils.Logger;
import org.jivesoftware.smack.XMPPException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class Permissions {
    String producerDID = "T3CS82NLOD9KIW8X";

    @Autowired
    private Logger logger;
    @Autowired
    private PermissionRepository permissionObject;

    @RequestMapping(
            value = "/permissions",
            method = RequestMethod.POST,
            consumes = "text/plain"
    )
    public void establishPermissions(@RequestBody String permissions, HttpServletResponse response) throws ParseException {
        // Read permissions from the consumer
        JSONParser parser = new JSONParser();
        JSONObject dataJSON = (JSONObject) parser.parse(permissions);
        JSONArray permissionsArray = (JSONArray) dataJSON.get("permissions");
        String consumerDID = (String) dataJSON.get("consumerDID");
        System.out.println(dataJSON.toJSONString());
        // Send permission document to
        try {
            CcsClient ccsClient = CcsClient.prepareClient("923983506811", "AAAA1yG1bXs:APA91bGTxsybnU8wi" +
                    "WzxzXuNgRpeYNjGred7PJIZRFaQJcqgzrFfQGA0jESW7c1Wo298KR3gor5lzMkam6uEJzb6QCHzw-GDWCIGcscu3XvNkTO5agE2QP" +
                    "TUrU9OM8EG8hqD33R7qCvJ", true);

            try {
                ccsClient.connect();
            } catch (XMPPException e) {
                System.out.println("System error" + e);
            }

            String messageId = Util.getUniqueMessageId();

            Map<String, String> dataPayload = new HashMap<String, String>();
            dataPayload.put(Util.PAYLOAD_ATTRIBUTE_MESSAGE, "Permissions");
            dataPayload.put("permissions", permissions);

            CcsOutMessage message = new CcsOutMessage("dHdgar30H_s:APA91bEjZA7OUNj98zinwq3Dh8gWualDjacfbEte4NaS8y59inXzLx-" +
                    "By30CagZIoym2NZ4kv9S2yvycmpMMHJUk0hkP3QsKiZ2eU8_3O4fO2zF_szduRj11jPOEwHpLpheHOYg9scOr", messageId, dataPayload);
            String jsonRequest = MessageHelper.createJsonOutMessage(message);
            ccsClient.send(jsonRequest);

            while(true) {
                if(ccsClient.permissionSet) {
                    System.out.println("CCSClient: " + ccsClient.permissionSet);
                    if(ccsClient.appPermissionResponse.equals("YES")) {
                        ccsClient.permissionSet = false;

                        List<PermissionSet> permissionSets = (List<PermissionSet>) ccsClient.approvedPermissions.get("permissions");
                        System.out.println("Set: " + permissionSets);
                        Permission permission = permissionObject.findByConsumerDID(consumerDID);
                        if(permission == null) {
                            System.out.println("Permission: " + permission);
                            permissionObject.save(new Permission(producerDID, consumerDID, permissionSets));
                        } else {
                            permission.setPermissions(permissionSets);
                            permissionObject.save(permission);
                        }

                        logger.log(consumerDID,"Permissions", "/permissions", "Accepted", "Permission to read/write data granted");

                        response.setStatus(200);
                        OutputStream out = response.getOutputStream();
                        out.write(ccsClient.approvedPermissions.toJSONString().getBytes());
                        out.close();
                        break;
                    } else {
                        logger.log(consumerDID,"Permissions", "/permissions", "Rejected", "Permissions to read/write data denied");

                        response.setStatus(401);
                        ccsClient.permissionSet = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(
            value = "/permissions",
            method = RequestMethod.GET
    )
    public void getPermissions(@RequestParam String did,  HttpServletResponse response) throws IOException {
        response.setStatus(200);
        OutputStream out = response.getOutputStream();
        if(did == null) {
            out.write(new String("Please enter DID'").getBytes());
        } else {
            out.write(permissionObject.findByConsumerDID(did).toString().getBytes());
        }
        out.close();
    }

    @RequestMapping(
            value = "/update/permissions",
            method = RequestMethod.POST,
            consumes = "text/plain"
    )
    public void updatePermissions(@RequestBody String document,  HttpServletResponse response) throws ParseException, IOException {
        JSONParser parser = new JSONParser();
        JSONObject dataJSON = (JSONObject) parser.parse(document);
        String consumerDID = (String) dataJSON.get("consumerDID");
        JSONArray permissions = (JSONArray) dataJSON.get("permissions");

        Permission permission = permissionObject.findByConsumerDID(consumerDID);
        permission.setPermissions(permissions);
        permissionObject.save(permission);

        logger.log(consumerDID,"Permissions", "/update/permissions", "Accepted", "Producer updated the permissions");

        String message = "Successfully updated the permission set";
        response.setStatus(200);
        OutputStream out = response.getOutputStream();
        out.write(message.getBytes());
        out.close();
    }
}
