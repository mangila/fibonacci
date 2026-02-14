package com.github.mangila.fibonacci.web.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
public class RedisPublisher {

    private static final Logger log = LoggerFactory.getLogger(RedisPublisher.class);

    private final JsonMapper jsonMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public RedisPublisher(JsonMapper jsonMapper,
                          StringRedisTemplate stringRedisTemplate) {
        this.jsonMapper = jsonMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void publish(String channel, FibonacciStreamOption option) {
        var json = jsonMapper.writeValueAsString(option);
        stringRedisTemplate.convertAndSend(channel, json);
    }

    public void publish(String channel, FibonacciIdOption option) {
        var json = jsonMapper.writeValueAsString(option);
        stringRedisTemplate.convertAndSend(channel, json);
    }
}
