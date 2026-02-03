package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.jobrunr.job.model.FibonacciComputeRequest;
import com.github.mangila.fibonacci.redis.FunctionName;
import com.github.mangila.fibonacci.redis.RedisKey;
import org.jobrunr.jobs.lambdas.JobRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.Pipeline;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

public class ProducerJobHandler implements JobRequestHandler<ProducerJobRequest> {

    private static final Logger log = LoggerFactory.getLogger(ProducerJobHandler.class);

    private final JsonMapper jsonMapper;
    private final JedisPooled jedis;
    private final FunctionName produceSequence;
    private final List<String> keys;

    public ProducerJobHandler(JsonMapper jsonMapper,
                              JedisPooled jedis,
                              FunctionName produceSequence,
                              RedisKey bloomFilter,
                              RedisKey queue) {
        this.jsonMapper = jsonMapper;
        this.jedis = jedis;
        this.produceSequence = produceSequence;
        this.keys = List.of(queue.value(), bloomFilter.value());
    }

    @Override
    public void run(ProducerJobRequest jobRequest) throws Exception {
        final var limit = jobRequest.limit();
        try (Pipeline pipeline = jedis.pipelined()) {
            for (int i = 1; i <= limit; i++) {
                final String iteration = Integer.toString(i);
                final FibonacciComputeRequest request = new FibonacciComputeRequest(i, jobRequest.algorithm());
                final String json = jsonMapper.writeValueAsString(request);
                pipeline.fcall(produceSequence.value(), keys, List.of(iteration, json));
            }
            pipeline.sync();
        }
    }
}
