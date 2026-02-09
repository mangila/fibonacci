package com.github.mangila.fibonacci.web.sse.service;

import com.github.mangila.fibonacci.postgres.FibonacciProjection;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.redis.RedisKey;
import com.github.mangila.fibonacci.web.shared.FibonacciMapper;
import com.github.mangila.fibonacci.web.sse.model.SseIdOption;
import com.github.mangila.fibonacci.web.sse.model.SseStreamOption;
import io.github.mangila.ensure4j.Ensure;
import org.intellij.lang.annotations.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;


@Component
public class SseMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(SseMessageHandler.class);

    private final JsonMapper jsonMapper;
    private final RedisKey stream;
    private final StringRedisTemplate stringRedisTemplate;
    private final PostgresRepository postgresRepository;
    private final FibonacciMapper mapper;
    private final SseSessionRegistry registry;

    public SseMessageHandler(JsonMapper jsonMapper,
                             RedisKey stream,
                             StringRedisTemplate stringRedisTemplate,
                             PostgresRepository postgresRepository,
                             FibonacciMapper mapper,
                             SseSessionRegistry registry) {
        this.jsonMapper = jsonMapper;
        this.stream = stream;
        this.stringRedisTemplate = stringRedisTemplate;
        this.postgresRepository = postgresRepository;
        this.mapper = mapper;
        this.registry = registry;
    }

    public void handleMessage(@Language("JSON") String message, String channel) {
        log.info("Handle message - {} - {}", message, channel);
        try {
            var node = jsonMapper.readTree(message);
            Ensure.isTrue(node.isObject(), "json node must be an object");
            if (isStreamOption(node)) {
                var sseStreamOption = jsonMapper.treeToValue(node, SseStreamOption.class);
                processStream(sseStreamOption, channel);
            } else if (isIdOption(node)) {
                var sseIdOption = jsonMapper.treeToValue(node, SseIdOption.class);
                processId(sseIdOption, channel);
            } else {
                log.warn("Unknown node property");
            }
        } catch (Exception e) {
            log.error("ERR - processing message", e);
        }
    }

    private static boolean isStreamOption(JsonNode node) {
        return node.hasNonNull("offset") && node.hasNonNull("limit");
    }

    private static boolean isIdOption(JsonNode node) {
        return node.hasNonNull("id");
    }

    private void processStream(SseStreamOption option, String channel) {
        final int offset = option.offset();
        final int limit = option.limit();
        var sessions = registry.getSessions(channel);
        if (!CollectionUtils.isEmpty(sessions)) {
            var readOptions = StreamReadOptions.empty()
                    .count(limit)
                    .noack()
                    .block(Duration.ofSeconds(5));
            // stream timeline structure: 1-0, 2-0, 3-0 .. etc
            var timeLineOffset = String.valueOf(offset).concat("-0");
            var readOffset = ReadOffset.from(timeLineOffset);
            var streamOptions = StreamOffset.create(stream.value(), readOffset);
            for (var session : sessions) {
                session.sendStreamStart();
            }
            //noinspection unchecked
            stringRedisTemplate.opsForStream()
                    .read(readOptions, streamOptions)
                    .forEach(record -> {
                        log.info("Received record: {}", record);
                        final var data = record.getValue();
                        @Language("JSON") final var member = data.get("member").toString();
                        final var event = jsonMapper.readValue(member, FibonacciProjection.class);
                        for (var session : sessions) {
                            session.send(event);
                        }
                    });
            for (var session : sessions) {
                session.sendStreamEnd();
            }
        } else {
            log.warn("No sse sessions for channel - {}", channel);
        }
    }

    private void processId(SseIdOption option, String channel) {
        final int id = option.id();
        var sessions = registry.getSessions(channel);
        if (!CollectionUtils.isEmpty(sessions)) {
            postgresRepository.queryById(id)
                    .ifPresentOrElse(entity -> {
                        var dto = mapper.toDto(entity);
                        for (var session : sessions) {
                            session.send(dto);
                        }
                    }, () -> log.warn("{} - not found", option));
        } else {
            log.warn("No sse sessions for channel - {}", channel);
        }
    }
}
