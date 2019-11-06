package edu.cmu.producerserver.pushnotifications.service.impl;

import edu.cmu.producerserver.pushnotifications.bean.CcsInMessage;
import edu.cmu.producerserver.pushnotifications.service.PayloadProcessor;

/**
 * Handles a user registration request
 */
public class RegisterProcessor implements PayloadProcessor {

    @Override
    public void handleMessage(CcsInMessage msg) {
        // TODO: handle the user registration. Keep in mind that a user name can
        // have more reg IDs associated. The messages IDs should be unique.
    }

}
