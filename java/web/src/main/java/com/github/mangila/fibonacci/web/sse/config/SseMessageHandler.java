package com.github.mangila.fibonacci.web.sse.config;

import com.github.mangila.fibonacci.postgres.FibonacciProjection;
import com.github.mangila.fibonacci.redis.RedisKey;
import com.github.mangila.fibonacci.web.sse.model.SseQuery;
import com.github.mangila.fibonacci.web.sse.service.SseSessionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;


@Component
public class SseMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(SseMessageHandler.class);

    private final JsonMapper jsonMapper;
    private final RedisKey stream;
    private final StringRedisTemplate stringRedisTemplate;
    private final SseSessionRegistry registry;

    public SseMessageHandler(JsonMapper jsonMapper,
                             RedisKey stream,
                             StringRedisTemplate stringRedisTemplate,
                             SseSessionRegistry registry) {
        this.jsonMapper = jsonMapper;
        this.stream = stream;
        this.stringRedisTemplate = stringRedisTemplate;
        this.registry = registry;
    }

    public void handleMessage(SseQuery message, String channel) {
        log.info("Received message: {}", message);
        log.info("Received channel: {}", channel);
        final var offset = message.offset();
        final var limit = message.limit();
        var sessions = registry.getSessions(channel);
        if (CollectionUtils.isEmpty(sessions)) {
            return;
        }
        var readOptions = StreamReadOptions.empty()
                .count(limit)
                .block(Duration.ofSeconds(5));
        var readOffset = ReadOffset.from(String.valueOf(offset).concat("-0"));
        var streamOptions = StreamOffset.create(stream.value(), readOffset);
        stringRedisTemplate.opsForStream().read(readOptions, streamOptions)
                .forEach(record -> {
                    log.info("Received record: {}", record);
                    final var data = record.getValue();
                    final var member = data.get("member");
                    final var event = jsonMapper.readValue(member.toString(), FibonacciProjection.class);
                    for (var session : sessions) {
                        session.send(event);
                    }
                });
    }

}
