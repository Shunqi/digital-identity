package edu.cmu.producerserver.pushnotifications;

import edu.cmu.producerserver.pushnotifications.service.PayloadProcessor;
import edu.cmu.producerserver.pushnotifications.service.impl.EchoProcessor;
import edu.cmu.producerserver.pushnotifications.service.impl.MessageProcessor;
import edu.cmu.producerserver.pushnotifications.service.impl.RegisterProcessor;
import edu.cmu.producerserver.pushnotifications.util.*;

/**
 * Manages the creation of different payload processors based on the desired
 * action
 */

public class ProcessorFactory {

    public static PayloadProcessor getProcessor(String action) {
        if (action == null) {
            throw new IllegalStateException("ProcessorFactory: Action must not be null! Options: 'REGISTER', 'ECHO', 'MESSAGE'");
        }
        if (action.equals(Util.BACKEND_ACTION_REGISTER)) {
            return new RegisterProcessor();
        } else if (action.equals(Util.BACKEND_ACTION_ECHO)) {
            return new EchoProcessor();
        } else if (action.equals(Util.BACKEND_ACTION_MESSAGE)) {
            return new MessageProcessor();
        }
        throw new IllegalStateException("ProcessorFactory: Unknown action: " + action + ". Options: 'REGISTER', 'ECHO', 'MESSAGE'");
    }
}
