package com.github.mangila.fibonacci.web.ws;

import com.github.mangila.fibonacci.core.model.FibonacciQuery;
import com.github.mangila.fibonacci.core.model.PgNotificationPayloadCollection;
import com.github.mangila.fibonacci.web.model.FibonacciDto;
import com.github.mangila.fibonacci.web.model.FibonacciProjectionDto;
import com.github.mangila.fibonacci.web.service.FibonacciService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

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
        try {
            template.convertAndSend("/topic/livestream", payload.value());
        } catch (Exception e) {
            log.error("Failed to send Websocket Frame", e);
        }
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
}
