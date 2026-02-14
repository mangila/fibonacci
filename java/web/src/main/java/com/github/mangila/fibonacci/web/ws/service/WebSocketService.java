package com.github.mangila.fibonacci.web.ws.service;

import com.github.mangila.fibonacci.web.shared.FibonacciIdOption;
import com.github.mangila.fibonacci.web.shared.FibonacciStreamOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private static final Logger log = LoggerFactory.getLogger(WebSocketService.class);

    public void queryById(FibonacciIdOption option) {
    }

    public void queryByStream(FibonacciStreamOption option) {
    }
}
