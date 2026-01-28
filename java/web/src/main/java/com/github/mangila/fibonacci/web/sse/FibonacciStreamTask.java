package com.github.mangila.fibonacci.web.sse;

import com.github.mangila.fibonacci.web.dto.FibonacciDtoMapper;
import com.github.mangila.fibonacci.web.dto.FibonacciProjectionDto;
import com.github.mangila.fibonacci.web.repository.FibonacciRepository;
import com.github.mangila.fibonacci.web.dto.SseFibonacciStreamQuery;
import com.github.mangila.fibonacci.web.sse.model.SseSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FibonacciStreamTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(FibonacciStreamTask.class);

    private final SimpleAsyncTaskExecutor ioAsyncTaskExecutor;
    private final FibonacciDtoMapper mapper;
    private final FibonacciRepository repository;
    private final SseFibonacciStreamQuery query;
    private final SseSession session;

    public FibonacciStreamTask(SimpleAsyncTaskExecutor ioAsyncTaskExecutor,
                               FibonacciDtoMapper mapper,
                               FibonacciRepository repository,
                               SseFibonacciStreamQuery query,
                               SseSession session) {
        this.ioAsyncTaskExecutor = ioAsyncTaskExecutor;
        this.mapper = mapper;
        this.repository = repository;
        this.query = query;
        this.session = session;
    }

    @Override
    public void run() {
        final int offset = query.offset();
        final int limit = query.limit();

        var latch = new CountDownLatch(1);
        final int backPressure = 10;
        final var queue = new ArrayBlockingQueue<FibonacciProjectionDto>(backPressure);

        var producerFuture = ioAsyncTaskExecutor.submitCompletable(() -> {
            try {
                repository.streamForList(offset, limit, stream -> {
                    stream.forEach(projection -> {
                        try {
                            var dto = mapper.map(projection);
                            queue.put(dto);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Producer interrupted");
                        }
                    });
                });
            } catch (Exception e) {
                log.error("Error while streaming results", e);
            } finally {
                log.info("Producer finished for session {} - {}", session.channel(), session.streamKey());
                latch.countDown();
            }
        });

        try {
            session.send("stream-start", "start", "");
            for (int i = 0; i <= limit; i++) {
                var dto = queue.poll(10, TimeUnit.SECONDS);
                if (queue.isEmpty() && latch.getCount() == 0) {
                    break;
                }
                if (dto != null) {
                    session.send("stream", dto.sequence(), dto);
                }
            }
            session.send("stream-end", "end", "");
        } catch (Exception e) {
            log.error("Error while streaming results", e);
            session.completeWithError(e);
        } finally {
            producerFuture.cancel(true);
        }
    }
}
