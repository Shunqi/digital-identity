package edu.cmu.consumerserver.routes;

import edu.cmu.consumerserver.model.Permission;
import edu.cmu.consumerserver.model.PermissionSet;
import edu.cmu.consumerserver.repository.PermissionRepository;
import edu.cmu.consumerserver.util.Connection;
import edu.cmu.consumerserver.util.DataClass;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
public class DataRetrieval {
    private final Connection connection = new Connection();
    private DataClass dc = new DataClass();

    private final PermissionRepository permissionRepository;

    public DataRetrieval(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @ResponseBody
    @GetMapping("/data/{dataRoute}")
    public void data(@PathVariable String dataRoute, HttpServletResponse response) {
        int status;
        try{
          // URL url = new URL(dc.getProducerHost() + "/data/" + dataRoute + "?authtoken=" + dc.getAuthToken());
           URL url = new URL(dc.getProducerHost() + "/data/" + dataRoute + "?authtoken=209522922f65e8e4125e5aec8fa078b9");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            connectToProducer(conn, response);
        }catch(IOException e){
            response.setStatus(401);
            e.printStackTrace();
        }
    }

    @ResponseBody
    @GetMapping("/thirdParty/{dataRoute}")
    public void thirdPartyGet(@PathVariable String dataRoute, @RequestParam(name = "thirdPartyDid") String thirdPartyDid, @RequestParam(name = "producerDid") String producerDid, HttpServletResponse response) {
        Permission permission = permissionRepository.findByProducerDIDAndConsumerDID(producerDid, dc.getConsumerDid());

        PermissionSet permissionSet = permission.getPermission(dataRoute);
        if (permissionSet == null || !permissionSet.isSharable()) {
            response.setStatus(401);
            return;
        }

        if(permissionSet.getThirdParty() != null && !permissionSet.isThirdParty(thirdPartyDid.trim())){
            response.setStatus(401);
            return;
        }
        try{
            URL url = new URL(dc.getProducerHost() + "/data/" + dataRoute + "?authtoken=" + dc.getAuthToken());
            //URL url = new URL(dc.getProducerHost() + "/data/" + dataRoute + "?authtoken=209522922f65e8e4125e5aec8fa078b9");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            connectToProducer(conn, response);
        }catch(IOException e){
            response.setStatus(401);
            e.printStackTrace();
        }
    }


    private void connectToProducer(HttpURLConnection conn, HttpServletResponse response) throws IOException {
        int status;
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
        PrintWriter out = response.getWriter();
        out.println(result);
        out.close();
    }
}
