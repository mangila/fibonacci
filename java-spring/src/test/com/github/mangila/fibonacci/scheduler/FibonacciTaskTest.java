package com.github.mangila.fibonacci.scheduler;

import com.github.mangila.fibonacci.PostgresTestContainerConfiguration;
import com.github.mangila.fibonacci.db.FibonacciRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(PostgresTestContainerConfiguration.class)
class FibonacciTaskTest {

    @MockitoBean
    private FibonacciScheduler _unused;

    @Autowired
    private FibonacciTask fibonacciTask;

    @MockitoSpyBean
    private FibonacciRepository repository;

    @Test
    void run() {
        fibonacciTask.run();
        verify(repository, Mockito.times(1)).hasSequence(any(Integer.class));
        verify(repository, Mockito.times(1)).insert(any());
    }
}