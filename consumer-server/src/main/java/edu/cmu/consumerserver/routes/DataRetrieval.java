package edu.cmu.consumerserver.routes;

import edu.cmu.consumerserver.util.Connection;
import edu.cmu.consumerserver.util.DataClass;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
public class DataRetrieval {
    private Connection connection = new Connection();
    private DataClass dc = new DataClass();

    @ResponseBody
    @GetMapping("/data/{dataRoute}")
    public void data(@PathVariable String dataRoute, HttpServletResponse response) {
        int status;
        try{
          // URL url = new URL(dc.getProducerHost() + "/data/" + dataRoute + "?authtoken=" + dc.getAuthToken());
           URL url = new URL(dc.getProducerHost() + "/data/" + dataRoute + "?authtoken=209522922f65e8e4125e5aec8fa078b9");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "text/plain");
            status = conn.getResponseCode();

            if(status != 200){
                response.setStatus(401);
                return;
            }
            response.setStatus(200);
            response.setContentType("text/plain;charset=UTF-8");
            String result = connection.inputStream(conn);
            dc.setData("test",result);
            PrintWriter out = response.getWriter();
            out.println(result);
            out.close();
        }catch(IOException e){
            response.setStatus(401);
            e.printStackTrace();
        }
    }
}
