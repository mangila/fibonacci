package com.github.mangila.fibonacci.jobrunr.job.consumer;

import com.github.mangila.fibonacci.jobrunr.job.consumer.compute.ComputeScheduler;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.postgres.test.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@PostgresTestContainer
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "app.job.producer.enabled=true",
                "app.job.consumer.enabled=false",
        })
class ConsumerJobHandlerTest {

    @Autowired
    private ConsumerJobHandler handler;

    @MockitoSpyBean
    private PostgresRepository repository;

    @MockitoSpyBean
    private ComputeScheduler computeScheduler;


    @Test
    void run() throws Exception {
        var jobRequest = new ConsumerJobRequest(10);
        handler.run(jobRequest);
    }
}