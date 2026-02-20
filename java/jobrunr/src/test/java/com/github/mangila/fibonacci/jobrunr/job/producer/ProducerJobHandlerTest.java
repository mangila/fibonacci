package com.github.mangila.fibonacci.jobrunr.job.producer;

import com.github.mangila.fibonacci.postgres.PostgresRepository;
import com.github.mangila.fibonacci.shared.FibonacciAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith({
        MockitoExtension.class,
        SpringExtension.class
})
public class ProducerJobHandlerTest {

    @MockitoBean
    private PostgresRepository postgresRepository;

    @Test
    void test() throws Exception {
        var handler = new ProducerJobHandler(postgresRepository);
        var request = new ProducerJobRequest(10, 50, FibonacciAlgorithm.ITERATIVE);
        handler.run(request);
        verify(postgresRepository, times(5)).batchInsertMetadata(anyList());
    }
}
