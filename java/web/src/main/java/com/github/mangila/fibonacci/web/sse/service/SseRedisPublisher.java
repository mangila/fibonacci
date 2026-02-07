package com.github.mangila.fibonacci.web.sse.service;

import com.github.mangila.fibonacci.web.sse.model.Option;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
public class SseRedisPublisher {

    private final JsonMapper jsonMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public SseRedisPublisher(JsonMapper jsonMapper,
                             StringRedisTemplate stringRedisTemplate) {
        this.jsonMapper = jsonMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void publish(String channel, Option option) {
        var json = jsonMapper.writeValueAsString(option);
        stringRedisTemplate.convertAndSend(channel, json);
    }
}
