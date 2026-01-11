package com.github.mangila.fibonacci.ws;

import com.github.mangila.fibonacci.db.FibonacciRepository;
import com.github.mangila.fibonacci.model.FibonacciResultEntity;
import com.github.mangila.fibonacci.model.FibonacciOption;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.dao.DataAccessException;
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
import java.util.Arrays;
import java.util.List;

@RestController
public class WebSocketController {

    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);

    private final FibonacciRepository repository;
    private final SimpleAsyncTaskExecutor ioAsyncTaskExecutor;
    private final ThreadPoolTaskExecutor computeAsyncTaskExecutor;
    private final SimpMessagingTemplate template;
    private final SimpUserRegistry simpUserRegistry;

    public WebSocketController(FibonacciRepository repository, @Qualifier("ioAsyncTaskExecutor") SimpleAsyncTaskExecutor ioAsyncTaskExecutor,
                               @Qualifier("computeAsyncTaskExecutor") ThreadPoolTaskExecutor computeAsyncTaskExecutor,
                               @Qualifier("brokerMessagingTemplate") SimpMessagingTemplate template,
                               SimpUserRegistry simpUserRegistry) {
        this.repository = repository;
        this.ioAsyncTaskExecutor = ioAsyncTaskExecutor;
        this.computeAsyncTaskExecutor = computeAsyncTaskExecutor;
        this.template = template;
        this.simpUserRegistry = simpUserRegistry;
    }

    @EventListener
    public void livestream(PGNotification[] pgNotifications) {
        List<String> notificationPayloads = Arrays.stream(pgNotifications)
                .map(PGNotification::getParameter)
                .toList();
        template.convertAndSend("/topic/livestream", notificationPayloads);
    }

    @MessageMapping("fibonacci")
    @SendToUser("/queue/fibonacci")
    public List<FibonacciResultEntity> queueFibonacciSequences(@Valid @NotNull FibonacciOption option, Principal principal) {
        log.info("Received request for fibonacci sequence {} from {}", option, principal.getName());
        return repository.queryForOption(option);
    }

    @MessageMapping("fibonacci/id")
    @SendToUser("/queue/fibonacci/id")
    public FibonacciResultEntity queryById(int id, Principal principal) {
        log.info("Received request for fibonacci sequence {} from {}", id, principal.getName());
        return repository.queryById(id);
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser(value = "/queue/errors", broadcast = false)
    public ProblemDetail handleValidationException(Exception ex, Principal principal) {
        log.error("Error handling WebSocket message from {}", principal.getName(), ex);
        return switch (ex) {
            case MethodArgumentNotValidException m -> handleMethodArgumentNotValidException(m);
            case DataAccessException d -> ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, d.getMessage());
            default -> ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "something went wrong");
        };
    }

    private ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Invalid request");

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
        problemDetail.setTitle("Validation Failed");
        return problemDetail;
    }
}
