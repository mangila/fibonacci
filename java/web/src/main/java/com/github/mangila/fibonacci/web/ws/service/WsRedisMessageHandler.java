package com.github.mangila.fibonacci.web.ws.service;

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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;

@Component
public class WsRedisMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(WsRedisMessageHandler.class);

    private final RedisKey stream;
    private final StringRedisTemplate stringRedisTemplate;
    private final FibonacciMapper mapper;
    private final PostgresRepository postgresRepository;
    private final JsonMapper jsonMapper;
    private final RedisMessageParser redisMessageParser;
    private final SimpMessagingTemplate template;

    public WsRedisMessageHandler(RedisKey stream,
                                 StringRedisTemplate stringRedisTemplate,
                                 FibonacciMapper mapper,
                                 PostgresRepository postgresRepository,
                                 JsonMapper jsonMapper,
                                 RedisMessageParser redisMessageParser,
                                 SimpMessagingTemplate template) {
        this.stream = stream;
        this.stringRedisTemplate = stringRedisTemplate;
        this.mapper = mapper;
        this.postgresRepository = postgresRepository;
        this.jsonMapper = jsonMapper;
        this.redisMessageParser = redisMessageParser;
        this.template = template;
    }

    public void handleWsMessage(@Language("JSON") String message, String channel) {
        log.info("Handle message - {} - {}", message, channel);
        var optionNode = redisMessageParser.determineOption(message);
        switch (optionNode.optionType()) {
            case STREAM_OPTION -> {
                var streamOption = jsonMapper.treeToValue(optionNode.node(), FibonacciStreamOption.class);
                processStreamOption(streamOption, channel);
            }
            case ID_OPTION -> {
                var idOption = jsonMapper.treeToValue(optionNode.node(), FibonacciIdOption.class);
                processIdOption(idOption, channel);
            }
            case UNKNOWN -> log.warn("Unknown option - {}", message);
        }
    }

    private void processStreamOption(FibonacciStreamOption option, String channel) {
        final int offset = option.offset();
        final int limit = option.limit();
        var readOptions = StreamReadOptions.empty()
                .count(limit)
                .noack()
                .block(Duration.ofSeconds(5));
        // stream timeline structure: 1-0, 2-0, 3-0 .. etc
        var timeLineOffset = String.valueOf(offset).concat("-0");
        var readOffset = ReadOffset.from(timeLineOffset);
        var streamOptions = StreamOffset.create(stream.value(), readOffset);
        //noinspection unchecked
        stringRedisTemplate.opsForStream()
                .read(readOptions, streamOptions)
                .forEach(record -> {
                    log.info("Received record: {}", record);
                    final var data = record.getValue();
                    @Language("JSON") final var member = data.get("member").toString();
                    final var event = jsonMapper.readValue(member, FibonacciProjection.class);
                    template.convertAndSendToUser(channel, "/queue/stream", event);
                });
    }

    private void processIdOption(FibonacciIdOption option, String channel) {
        final int id = option.id();
        postgresRepository.queryById(id)
                .ifPresentOrElse(entity -> {
                    var dto = mapper.toDto(entity);
                    template.convertAndSendToUser(channel, "/queue/id", dto);
                }, () -> log.warn("{} - not found", option));
    }

}
