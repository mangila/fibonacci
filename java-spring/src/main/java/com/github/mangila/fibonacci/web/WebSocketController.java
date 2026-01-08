package com.github.mangila.fibonacci.web;

import com.github.mangila.fibonacci.model.FibonacciOption;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class WebSocketController {

    private final SimpMessagingTemplate template;
    private final SimpUserRegistry simpUserRegistry;

    public WebSocketController(@Qualifier("brokerMessagingTemplate") SimpMessagingTemplate template,
                               SimpUserRegistry simpUserRegistry) {
        this.template = template;
        this.simpUserRegistry = simpUserRegistry;
    }

    @MessageMapping("fibonacci")
    public void generateFibonacciSequences(@Payload FibonacciOption option, Principal principal) {
        template.convertAndSend("/topic/fibonacci", option);
        template.convertAndSend("/topic/fibonacci", option);
    }

    @MessageExceptionHandler
    public Exception handleException(Exception exception) {
        return exception;
    }

}
