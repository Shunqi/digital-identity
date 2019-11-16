package edu.cmu.producerserver.utils;

import edu.cmu.producerserver.model.Log;
import edu.cmu.producerserver.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Logger {
    @Autowired
    private LogRepository logger;

    public Logger() {

    }

    public void log(String did, String type, String route, String status, String message) {
        logger.save(new Log(did, type, route, status, message));
    }
}
