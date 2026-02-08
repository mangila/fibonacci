package com.github.mangila.fibonacci.web.sse.service;

import com.github.mangila.fibonacci.web.sse.model.SseIdOption;
import com.github.mangila.fibonacci.web.sse.model.SseStreamOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
public class SseRedisPublisher {

    private static final Logger log = LoggerFactory.getLogger(SseRedisPublisher.class);

    private final JsonMapper jsonMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public SseRedisPublisher(JsonMapper jsonMapper,
                             StringRedisTemplate stringRedisTemplate) {
        this.jsonMapper = jsonMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void publish(String channel, SseStreamOption option) {
        var json = jsonMapper.writeValueAsString(option);
        stringRedisTemplate.convertAndSend(channel, json);
    }

    public void publish(String channel, SseIdOption option) {
        var json = jsonMapper.writeValueAsString(option);
        stringRedisTemplate.convertAndSend(channel, json);
    }
}
