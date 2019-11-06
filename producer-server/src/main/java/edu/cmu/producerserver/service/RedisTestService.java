package edu.cmu.producerserver.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisTestService {

    private final StringRedisTemplate redisTemplate;

    public RedisTestService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void put(String key, String value) {  redisTemplate.opsForValue().set(key, value);}

    public void setTTL(String key, long days) {
        redisTemplate.expire(key, days, TimeUnit.DAYS);
    }
}
