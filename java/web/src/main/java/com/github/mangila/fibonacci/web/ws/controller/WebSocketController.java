package com.github.mangila.fibonacci.web.ws.controller;

import com.github.mangila.fibonacci.web.shared.FibonacciIdOption;
import com.github.mangila.fibonacci.web.shared.FibonacciStreamOption;
import com.github.mangila.fibonacci.web.ws.service.WebSocketService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketController {

    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);

    private final WebSocketService service;

    public WebSocketController(WebSocketService service) {
        this.service = service;
    }

    @MessageMapping("stream")
    public void wsQueryByStream(@NotNull @Valid FibonacciStreamOption option, Principal principal) {
        log.info("ws query by stream - {} - {}", option, principal.getName());
        service.queryByStream(option);
    }

    @MessageMapping("id")
    public void wsQueryById(@NotNull @Valid FibonacciIdOption option, Principal principal) {
        log.info("ws query by id - {} - {}", option, principal.getName());
        service.queryById(option);
    }
}
