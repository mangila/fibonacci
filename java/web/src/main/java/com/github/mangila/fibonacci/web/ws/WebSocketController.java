package com.github.mangila.fibonacci.web.ws;

import com.github.mangila.fibonacci.core.model.FibonacciQuery;
import com.github.mangila.fibonacci.web.model.FibonacciDto;
import com.github.mangila.fibonacci.web.service.FibonacciService;
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
    private final FibonacciService service;
    private final SimpMessagingTemplate template;

    public WebSocketController(SimpleAsyncTaskExecutor ioAsyncTaskExecutor,
                               FibonacciService service,
                               SimpMessagingTemplate template) {
        this.ioAsyncTaskExecutor = ioAsyncTaskExecutor;
        this.service = service;
        this.template = template;
    }

    @MessageMapping("fibonacci/list")
    public void wsQueryForList(@Valid @NotNull FibonacciQuery query, Principal principal) {
        log.info("Received request for fibonacci sequence {} from {}", query, principal.getName());
        ioAsyncTaskExecutor.submitCompletable(() -> {
            service.streamForList(query, stream -> {
                stream.forEach(projection -> {
                    log.info("Sending fibonacci sequence {} to {}", projection, principal.getName());
                    template.convertAndSendToUser(principal.getName(), "queue/fibonacci/list", projection);
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        });
    }

    @MessageMapping("fibonacci/id")
    @SendToUser("/queue/fibonacci/id")
    public FibonacciDto wsQueryById(@Min(1) int id, Principal principal) {
        log.info("Received request for fibonacci sequence {} from {}", id, principal.getName());
        FibonacciDto dto = service.queryById(id);
        return dto;
    }
}
