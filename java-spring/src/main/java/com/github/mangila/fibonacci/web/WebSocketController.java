package com.github.mangila.fibonacci.web;

import com.github.mangila.fibonacci.api.FibonacciTask;
import com.github.mangila.fibonacci.model.FibonacciOption;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class WebSocketController {

    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);

    private final SimpleAsyncTaskExecutor ioAsyncTaskExecutor;
    private final ThreadPoolTaskExecutor computeAsyncTaskExecutor;
    private final SimpMessagingTemplate template;
    private final SimpUserRegistry simpUserRegistry;

    public WebSocketController(@Qualifier("ioAsyncTaskExecutor") SimpleAsyncTaskExecutor ioAsyncTaskExecutor,
                               @Qualifier("computeAsyncTaskExecutor") ThreadPoolTaskExecutor computeAsyncTaskExecutor,
                               @Qualifier("brokerMessagingTemplate") SimpMessagingTemplate template,
                               SimpUserRegistry simpUserRegistry) {
        this.ioAsyncTaskExecutor = ioAsyncTaskExecutor;
        this.computeAsyncTaskExecutor = computeAsyncTaskExecutor;
        this.template = template;
        this.simpUserRegistry = simpUserRegistry;
    }

    @MessageMapping("fibonacci")
    public void generateFibonacciSequences(@Valid @NotNull FibonacciOption option, Principal principal) {
        log.info("Received request for fibonacci sequence {} from {}", option, principal.getName());
        final String destination = String.format("/user/%s/queue/results", principal.getName());
        // Run on a platform thread for computation
        var unused = computeAsyncTaskExecutor.submitCompletable(new FibonacciTask(template, destination, option));
    }

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    @SendToUser("/queue/errors")
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex, Principal principal) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Invalid request");

        log.error("Error handling WebSocket message from {}: {}", principal.getName(), errorMessage);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
        problemDetail.setTitle("Validation Failed");
        return problemDetail;
    }
}
