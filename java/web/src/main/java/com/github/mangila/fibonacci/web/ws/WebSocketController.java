package com.github.mangila.fibonacci.web.ws;

import com.github.mangila.fibonacci.core.entity.FibonacciEntity;
import com.github.mangila.fibonacci.web.repository.FibonacciRepository;
import com.github.mangila.fibonacci.web.dto.WsFibonacciStreamQuery;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.concurrent.TimeUnit;

@RestController
@Validated
public class WebSocketController {

    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);

    private final SimpleAsyncTaskExecutor ioAsyncTaskExecutor;
    private final FibonacciRepository repository;
    private final SimpMessagingTemplate template;

    public WebSocketController(SimpleAsyncTaskExecutor ioAsyncTaskExecutor,
                               FibonacciRepository repository,
                               SimpMessagingTemplate template) {
        this.ioAsyncTaskExecutor = ioAsyncTaskExecutor;
        this.repository = repository;
        this.template = template;
    }


    @MessageMapping("fibonacci/stream")
    public void wsQueryForList(@Valid @NotNull WsFibonacciStreamQuery query, Principal principal) {
        log.info("Received request for fibonacci sequence {} from {}", query, principal.getName());
        final var offset = query.offset();
        final var limit = query.limit();
        final var delayInMillis = query.delayInMillis();
        ioAsyncTaskExecutor.submitCompletable(() -> {
            repository.streamForList(offset, limit, stream -> {
                stream.forEach(projection -> {
                    log.info("Sending fibonacci sequence {} to {}", projection, principal.getName());
                    template.convertAndSendToUser(principal.getName(), "queue/fibonacci/stream", projection);
                    try {
                        TimeUnit.MILLISECONDS.sleep(delayInMillis);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        });
    }

    @MessageMapping("fibonacci/id")
    @SendToUser("/queue/fibonacci/id")
    public FibonacciEntity wsQueryById(@Min(1) int id, Principal principal) {
        log.info("Received request for fibonacci sequence {} from {}", id, principal.getName());
        FibonacciEntity entity = repository.queryById(id).orElseThrow();
        return entity;
    }
}
