package com.github.mangila.fibonacci.web.sse.service;

import com.github.mangila.fibonacci.postgres.FibonacciProjection;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.redis.RedisKey;
import com.github.mangila.fibonacci.web.shared.FibonacciIdOption;
import com.github.mangila.fibonacci.web.shared.FibonacciMapper;
import com.github.mangila.fibonacci.web.shared.FibonacciStreamOption;
import com.github.mangila.fibonacci.web.shared.RedisMessageParser;
import org.intellij.lang.annotations.Language;
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
public class SseRedisMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(SseRedisMessageHandler.class);

    private final JsonMapper jsonMapper;
    private final RedisMessageParser redisMessageParser;
    private final RedisKey stream;
    private final StringRedisTemplate stringRedisTemplate;
    private final PostgresRepository postgresRepository;
    private final FibonacciMapper mapper;
    private final SseSessionRegistry registry;

    public SseRedisMessageHandler(JsonMapper jsonMapper,
                                  RedisMessageParser redisMessageParser,
                                  RedisKey stream,
                                  StringRedisTemplate stringRedisTemplate,
                                  PostgresRepository postgresRepository,
                                  FibonacciMapper mapper,
                                  SseSessionRegistry registry) {
        this.jsonMapper = jsonMapper;
        this.redisMessageParser = redisMessageParser;
        this.stream = stream;
        this.stringRedisTemplate = stringRedisTemplate;
        this.postgresRepository = postgresRepository;
        this.mapper = mapper;
        this.registry = registry;
    }

    public void handleSseMessage(@Language("JSON") String message, String channel) {
        log.info("Handle message - {} - {}", message, channel);
        try {
            var optionNode = redisMessageParser.determineOption(message);
            switch (optionNode.optionType()) {
                case STREAM_OPTION -> {
                    var sseStreamOption = jsonMapper.treeToValue(optionNode.node(), FibonacciStreamOption.class);
                    processStream(sseStreamOption, channel);
                }
                case ID_OPTION -> {
                    var sseIdOption = jsonMapper.treeToValue(optionNode.node(), FibonacciIdOption.class);
                    processId(sseIdOption, channel);
                }
                case UNKNOWN -> log.warn("Unknown option - {}", message);
            }
        } catch (Exception e) {
            log.error("Error while processing message: {}", message, e);
        }
    }

    private void processStream(FibonacciStreamOption option, String channel) {
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

    private void processId(FibonacciIdOption option, String channel) {
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
