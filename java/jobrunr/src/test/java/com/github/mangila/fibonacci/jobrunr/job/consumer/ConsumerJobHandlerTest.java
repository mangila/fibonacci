package com.github.mangila.fibonacci.jobrunr.job.consumer;

import com.github.mangila.fibonacci.shared.FibonacciAlgorithm;
import com.github.mangila.fibonacci.jobrunr.job.model.FibonacciComputeRequest;
import com.github.mangila.fibonacci.postgres.test.PostgresTestContainer;
import com.github.mangila.fibonacci.redis.RedisKey;
import com.github.mangila.fibonacci.redis.RedisRepository;
import com.github.mangila.fibonacci.redis.test.RedisTestContainer;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import redis.clients.jedis.UnifiedJedis;
import tools.jackson.databind.json.JsonMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@PostgresTestContainer
@RedisTestContainer
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "app.job.consumer.enabled=true",
                "app.job.consumer.limit=50"
        })
class ConsumerJobHandlerTest {

    @Autowired
    private ConsumerJobHandler handler;

    @Autowired
    private UnifiedJedis jedis;

    @Autowired
    private RedisKey queue;

    @MockitoSpyBean
    private RedisRepository repository;

    @MockitoSpyBean
    private JsonMapper jsonMapper;

    @Language("JSON")
    private String payload;

    @BeforeEach
    void setUp() {
        this.payload = jsonMapper.writeValueAsString(new FibonacciComputeRequest(10, FibonacciAlgorithm.ITERATIVE));
    }

    @Test
    void run() throws Exception {
        int limit = 10;
        jedis.rpush(queue.value(), payload);
        var request = new ConsumerJobRequest(limit);
        handler.run(request);
        verify(repository, times(limit)).popQueue(any());
        verify(jsonMapper, times(1)).readValue(payload, FibonacciComputeRequest.class);
        verify(repository, times(1)).checkBloomFilter(any(RedisKey.class), anyInt());
        verify(repository, times(1)).addBloomFilter(any(RedisKey.class), anyInt());
        reset(repository, jsonMapper);
        jedis.rpush(queue.value(), payload);
        handler.run(request);
        verify(repository, times(limit)).popQueue(any());
        verify(jsonMapper, times(1)).readValue(payload, FibonacciComputeRequest.class);
        verify(repository, times(1)).checkBloomFilter(any(RedisKey.class), anyInt());
        verify(repository, times(0)).addBloomFilter(any(RedisKey.class), anyInt());
    }
}