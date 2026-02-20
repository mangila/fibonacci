package com.github.mangila.fibonacci.jobrunr.job.consumer;

import com.github.mangila.fibonacci.jobrunr.job.consumer.compute.ComputeScheduler;
import com.github.mangila.fibonacci.postgres.FibonacciMetadataProjection;
import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.shared.FibonacciAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith({
        MockitoExtension.class,
        SpringExtension.class
})
public class ConsumerJobHandlerTest {

    @MockitoBean
    private PostgresRepository postgresRepository;

    @MockitoBean
    private ComputeScheduler computeScheduler;

    @Test
    void test() throws Exception {
        when(computeScheduler.schedule(any())).thenReturn(UUID.randomUUID());
        var stream = Stream.of(
                new FibonacciMetadataProjection(1, false, FibonacciAlgorithm.ITERATIVE.name()),
                new FibonacciMetadataProjection(2, false, FibonacciAlgorithm.ITERATIVE.name())
        );
        doAnswer(invocation -> {
            Consumer<Stream<FibonacciMetadataProjection>> consumer = invocation.getArgument(1);
            consumer.accept(stream);
            return null;
        }).when(postgresRepository).streamMetadataWhereComputedFalseLocked(anyInt(), any());
        var handler = new ConsumerJobHandler(computeScheduler, postgresRepository);
        var request = new ConsumerJobRequest(10);
        handler.run(request);
        verify(computeScheduler, times(2)).schedule(any());
    }
}
