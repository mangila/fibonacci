package com.github.mangila.fibonacci.scheduler.scheduler;

import com.github.mangila.fibonacci.scheduler.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@PostgresTestContainer
@SpringBootTest(properties = {
        "app.fibonacci.offset=1",
        "app.fibonacci.limit=5",
        "app.fibonacci.delay=300ms"
})
class SchedulerTest {

    @MockitoSpyBean
    private Scheduler scheduler;

    @Test
    void compute() {
        await()
                .atMost(5, TimeUnit.SECONDS);
    }
}