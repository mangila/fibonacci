package com.github.mangila.fibonacci.web.ws.service;

import com.github.mangila.fibonacci.web.shared.FibonacciIdOption;
import com.github.mangila.fibonacci.web.shared.FibonacciStreamOption;
import com.github.mangila.fibonacci.web.shared.RedisPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class WebSocketService {

    private static final Logger log = LoggerFactory.getLogger(WebSocketService.class);

    private final RedisPublisher redisPublisher;

    public WebSocketService(RedisPublisher redisPublisher) {
        this.redisPublisher = redisPublisher;
    }

    public void queryById(Principal principal, FibonacciIdOption option) {
        redisPublisher.publish(principal.getName(), option);
    }

    public void queryByStream(Principal principal, FibonacciStreamOption option) {
        redisPublisher.publish(principal.getName(), option);
    }
}
