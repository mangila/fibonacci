package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.jobrunr.job.model.FibonacciComputeRequest;
import com.github.mangila.fibonacci.redis.FunctionName;
import com.github.mangila.fibonacci.redis.RedisKey;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@ConditionalOnProperty(prefix = "app.produce", name = "enabled", havingValue = "true")
@Component
public class FibonacciProduceJobHandler implements JobRequestHandler<FibonacciProduceJobRequest> {

    private static final Logger log = LoggerFactory.getLogger(FibonacciProduceJobHandler.class);

    private final JsonMapper jsonMapper;
    private final JedisConnectionFactory jedisConnectionFactory;
    private final FunctionName produceSequence;
    private final List<String> keys;

    public FibonacciProduceJobHandler(JsonMapper jsonMapper,
                                      JedisConnectionFactory jedisConnectionFactory,
                                      FunctionName produceSequence,
                                      RedisKey bloomFilter,
                                      RedisKey queue) {
        this.jsonMapper = jsonMapper;
        this.jedisConnectionFactory = jedisConnectionFactory;
        this.produceSequence = produceSequence;
        this.keys = List.of(queue.value(), bloomFilter.value());
    }

    @Override
    public void run(FibonacciProduceJobRequest jobRequest) throws Exception {
        final var limit = jobRequest.limit();
        try (Jedis jedis = (Jedis) jedisConnectionFactory.getConnection().getNativeConnection()) {
            var pipeline = jedis.pipelined();
            for (int i = 1; i < limit; i++) {
                final String iteration = Integer.toString(i);
                final FibonacciComputeRequest request = new FibonacciComputeRequest(i, jobRequest.algorithm());
                final String json = jsonMapper.writeValueAsString(request);
                pipeline.fcall(produceSequence.value(), keys, List.of(iteration, json));
            }
            pipeline.sync();
        }
    }
}
