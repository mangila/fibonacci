package com.github.mangila.fibonacci.ws;

import com.github.mangila.fibonacci.db.FibonacciRepository;
import com.github.mangila.fibonacci.event.PgNotificationPayload;
import com.github.mangila.fibonacci.model.FibonacciOption;
import com.github.mangila.fibonacci.model.FibonacciResultEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class WebSocketController {

    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);

    private final FibonacciRepository repository;
    private final SimpMessagingTemplate template;

    public WebSocketController(FibonacciRepository repository,
                               @Qualifier("brokerMessagingTemplate") SimpMessagingTemplate template) {
        this.repository = repository;
        this.template = template;
    }

    @EventListener
    public void wsLivestream(PgNotificationPayload payload) {
        template.convertAndSend("/topic/livestream", payload.value());
    }

    @MessageMapping("fibonacci")
    @SendToUser("/queue/fibonacci")
    public List<FibonacciResultEntity> queueFibonacciSequences(@Valid @NotNull FibonacciOption option, Principal principal) {
        log.info("Received request for fibonacci sequence {} from {}", option, principal.getName());
        return repository.queryForList(option);
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
