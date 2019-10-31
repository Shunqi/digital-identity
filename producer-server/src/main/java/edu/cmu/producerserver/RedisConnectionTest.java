package edu.cmu.producerserver;

import edu.cmu.producerserver.service.RedisTestService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class RedisConnectionTest implements CommandLineRunner {
    private final RedisTestService rts;

    public RedisConnectionTest(RedisTestService rts) {
        this.rts = rts;
    }

    @Override
    public void run(String... args) throws Exception {
        String value = rts.getValue("shunqi");
        System.out.println(value);
    }
}
