package com.github.mangila.fibonacci.api;

import com.github.mangila.fibonacci.model.FibonacciOption;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;

class FibonacciTaskTest {

    @Test
    void run() {
        SimpMessagingTemplate mockTemplate = mock(SimpMessagingTemplate.class);
        FibonacciOption option = new FibonacciOption(1, 10_000);
        FibonacciTask task = new FibonacciTask(mockTemplate, "", option);

        var s = new StopWatch();
        s.start("compute run");
        task.run();
        s.stop();

        s.start("cache run");
        task.run();
        s.stop();

        System.out.println(s.prettyPrint(TimeUnit.MILLISECONDS));
    }
}