package edu.cmu.producerserver.pushnotifications.service.impl;

import edu.cmu.producerserver.pushnotifications.bean.CcsInMessage;
import edu.cmu.producerserver.pushnotifications.bean.CcsOutMessage;
import edu.cmu.producerserver.pushnotifications.CcsClient;
import edu.cmu.producerserver.pushnotifications.MessageHelper;
import edu.cmu.producerserver.pushnotifications.service.PayloadProcessor;
import edu.cmu.producerserver.pushnotifications.util.*;

/**
 * Handles an upstream message request
 */
public class MessageProcessor implements PayloadProcessor {

    @Override
    public void handleMessage(CcsInMessage inMessage) {
        CcsClient client = CcsClient.getInstance();
        String messageId = Util.getUniqueMessageId();
        String to = inMessage.getDataPayload().get(Util.PAYLOAD_ATTRIBUTE_RECIPIENT);

        // TODO: handle the data payload sent to the client device. Here, I just
        // resend the incoming message.
        CcsOutMessage outMessage = new CcsOutMessage(to, messageId, inMessage.getDataPayload());
        String jsonRequest = MessageHelper.createJsonOutMessage(outMessage);
        client.send(jsonRequest);
    }

}