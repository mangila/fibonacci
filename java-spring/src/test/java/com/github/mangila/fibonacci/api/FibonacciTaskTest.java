package com.github.mangila.fibonacci.api;

import com.github.mangila.fibonacci.model.FibonacciOption;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StopWatch;

import java.security.Principal;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FibonacciTaskTest {

    @Test
    void run() {
        SimpMessagingTemplate mockTemplate = mock(SimpMessagingTemplate.class);
        Principal mockPrincipal = mock(Principal.class);
        FibonacciOption option = new FibonacciOption(1, 100);
        FibonacciTask task = new FibonacciTask(mockTemplate, mockPrincipal, option);

        var s = new StopWatch();
        when(mockPrincipal.getName()).thenReturn("mock");
        s.start("first run");
        task.run();
        s.stop();

        s.start("second run");
        task.run();
        s.stop();

        System.out.println(s.prettyPrint(TimeUnit.MILLISECONDS));
    }
}