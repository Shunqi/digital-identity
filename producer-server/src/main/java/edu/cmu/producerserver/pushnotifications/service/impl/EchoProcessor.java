package edu.cmu.producerserver.pushnotifications.service.impl;

import edu.cmu.producerserver.pushnotifications.bean.CcsInMessage;
import edu.cmu.producerserver.pushnotifications.bean.CcsOutMessage;
import edu.cmu.producerserver.pushnotifications.CcsClient;
import edu.cmu.producerserver.pushnotifications.MessageHelper;
import edu.cmu.producerserver.pushnotifications.service.PayloadProcessor;
import edu.cmu.producerserver.pushnotifications.util.*;

/**
 * Handles an echo request
 */
public class EchoProcessor implements PayloadProcessor {

    @Override
    public void handleMessage(CcsInMessage inMessage) {
        CcsClient client = CcsClient.getInstance();
        String messageId = Util.getUniqueMessageId();
        String to = inMessage.getFrom();

        // Send the incoming message to the the device that made the request
        CcsOutMessage outMessage = new CcsOutMessage(to, messageId, inMessage.getDataPayload());
        String jsonRequest = MessageHelper.createJsonOutMessage(outMessage);
        client.send(jsonRequest);
    }

}
