package edu.cmu.producerserver.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisTestService {

    private final StringRedisTemplate redisTemplate;

    public RedisTestService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getValueTest() {
        return redisTemplate.opsForValue().get("shunqi");
    }
}
