package edu.cmu.producerserver.routes;

import edu.cmu.producerserver.pushnotifications.CcsClient;
import edu.cmu.producerserver.pushnotifications.MessageHelper;
import edu.cmu.producerserver.pushnotifications.bean.CcsOutMessage;
import edu.cmu.producerserver.pushnotifications.util.Util;
import org.jivesoftware.smack.XMPPException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Permissions {

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
                    if(ccsClient.appPermissionResponse.equals("YES")) {
                        System.out.println("Clicked yes");
                        ccsClient.permissionSet = false;
                        response.setStatus(200);
                        OutputStream out = response.getOutputStream();
                        out.write(ccsClient.approvedPermissions.toJSONString().getBytes());
                        out.close();
                        break;
                    } else {
                        System.out.println("Clicked no");
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
}
