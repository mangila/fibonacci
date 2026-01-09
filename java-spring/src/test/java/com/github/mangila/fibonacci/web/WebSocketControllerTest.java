package com.github.mangila.fibonacci.web;

import com.github.mangila.fibonacci.model.FibonacciOption;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.*;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketControllerTest {

    private static final Logger log = LoggerFactory.getLogger(WebSocketControllerTest.class);
    @LocalServerPort
    private int port;
    private String url;

    private WebSocketStompClient stompClient;

    @BeforeEach
    void setUp() {
        this.url = "ws://localhost:" + port + "/stomp";
        this.stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        List<MessageConverter> converters = new ArrayList<>();
        converters.add(new StringMessageConverter());
        converters.add(new JacksonJsonMessageConverter());
        converters.add(new ByteArrayMessageConverter());
        this.stompClient.setMessageConverter(new CompositeMessageConverter(converters));
    }

    @Test
    void generateFibonacciSequences() throws InterruptedException {
        StompSession session = stompClient
                .connectAsync(url, new StompSessionHandlerAdapter() {
                })
                .join();
        FibonacciOption option = new FibonacciOption(1, 100);
        CountDownLatch latch = new CountDownLatch(option.limit());
        session.subscribe("/user/queue/results", new StompSessionHandlerAdapter() {
            @Override
            public void handleFrame(StompHeaders headers, @Nullable Object payload) {
                log.info("Received headers: {} - Payload: {}", headers, payload);
                latch.countDown();
            }

            @Override
            public Type getPayloadType(StompHeaders headers) {
                return byte[].class;
            }
        });
        session.send("/app/fibonacci", option);
        await()
                .atMost(Duration.ofSeconds(5))
                .until(() -> latch.getCount() == 0);
    }

    @Test
    void failValidation() throws InterruptedException {
        StompSession session = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
                })
                .join();
        FibonacciOption option = new FibonacciOption(1, -10);
        CountDownLatch latch = new CountDownLatch(1);
        session.subscribe("/user/queue/errors", new StompSessionHandlerAdapter() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return byte[].class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                var errorJson = new String((byte[]) payload);
                assertThat(errorJson).contains("limit: must be greater than or equal to 1");
                latch.countDown();
            }

            @Override
            public void handleException(StompSession session, @Nullable StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                log.error("Error handling WebSocket message: {}", exception.getMessage());
                super.handleException(session, command, headers, payload, exception);
            }
        });
        session.send("/app/fibonacci", option);
        await()
                .atMost(Duration.ofSeconds(5))
                .until(() -> latch.getCount() == 0);
    }
}