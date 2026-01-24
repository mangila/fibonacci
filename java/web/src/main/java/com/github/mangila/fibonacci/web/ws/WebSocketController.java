package com.github.mangila.fibonacci.web.ws;

import com.github.mangila.fibonacci.core.model.PgNotificationPayloadCollection;
import com.github.mangila.fibonacci.web.model.FibonacciDto;
import com.github.mangila.fibonacci.web.model.FibonacciProjectionDto;
import com.github.mangila.fibonacci.core.model.FibonacciQuery;
import com.github.mangila.fibonacci.web.service.FibonacciService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@Validated
public class WebSocketController {

    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);

    private final FibonacciService service;
    private final SimpMessagingTemplate template;

    public WebSocketController(FibonacciService service,
                               SimpMessagingTemplate template) {
        this.service = service;
        this.template = template;
    }

    @EventListener
    public void wsLivestream(PgNotificationPayloadCollection payload) {
        template.convertAndSend("/topic/livestream", payload);
    }

    @MessageMapping("fibonacci/list")
    @SendToUser("/queue/fibonacci/list")
    public List<FibonacciProjectionDto> wsQueryForList(@Valid @NotNull FibonacciQuery query, Principal principal) {
        log.info("Received request for fibonacci sequence {} from {}", query, principal.getName());
        List<FibonacciProjectionDto> projections = service.queryForList(query);
        return projections;
    }

    @MessageMapping("fibonacci/id")
    @SendToUser("/queue/fibonacci/id")
    public FibonacciDto wsQueryById(@Min(1) int id, Principal principal) {
        log.info("Received request for fibonacci sequence {} from {}", id, principal.getName());
        FibonacciDto dto = service.queryById(id);
        return dto;
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser(value = "/queue/errors", broadcast = false)
    public ProblemDetail handleValidationException(Exception ex, Principal principal) {
        log.error("Error handling WebSocket message from {}", principal.getName(), ex);
        if (ex instanceof MethodArgumentNotValidException m) {
            return handleMethodArgumentNotValidException(m);
        } else if (ex instanceof NoSuchElementException d) {
            return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, d.getMessage());
        } else {
            return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "something went wrong");
        }
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
