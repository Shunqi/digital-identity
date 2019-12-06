package edu.cmu.producerserver.routes;

import edu.cmu.producerserver.model.Log;
import edu.cmu.producerserver.repository.LogRepository;
import edu.cmu.producerserver.utils.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
public class Logs {
    @Autowired
    private LogRepository logrepository;

    @Autowired
    private Logger logger;

    @RequestMapping(
            value = "/logs",
            method = RequestMethod.GET
    )
    public void getLogs(HttpServletResponse response) throws IOException {
        response.setStatus(200);
        OutputStream out = response.getOutputStream();
        List<Log> logs = logrepository.findAll();
        Collections.reverse(logs);
        out.write(logs.toString().getBytes());
        out.close();
    }
}
