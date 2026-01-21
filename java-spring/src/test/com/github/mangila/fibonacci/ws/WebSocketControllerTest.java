package com.github.mangila.fibonacci.ws;

import com.github.mangila.fibonacci.PostgresTestContainerConfiguration;
import com.github.mangila.fibonacci.model.dto.FibonacciOption;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.ProblemDetail;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
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
@Import(PostgresTestContainerConfiguration.class)
class WebSocketControllerTest {

    private static final Logger log = LoggerFactory.getLogger(WebSocketControllerTest.class);

    @LocalServerPort
    private int port;
    private String url;

    private WebSocketStompClient stompClient;

    @BeforeEach
    void setUp() {
        this.url = "ws://localhost:" + port + "/stomp";
        var ws = new StandardWebSocketClient();
        this.stompClient = new WebSocketStompClient(ws);
        List<MessageConverter> converters = new ArrayList<>();
        converters.add(new JacksonJsonMessageConverter());
        this.stompClient.setMessageConverter(new CompositeMessageConverter(converters));
    }

    @Test
    void wsLivestream() {
        StompSession session = stompClient
                .connectAsync(url, new StompSessionHandlerAdapter() {
                })
                .join();
        CountDownLatch latch = new CountDownLatch(2);
        session.subscribe("/topic/livestream", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return List.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, @Nullable Object payload) {
                log.info("Received headers: {} - Payload: {}", headers, payload);
                assertThat(payload).isNotNull();
                latch.countDown();
            }
        });
        await()
                .atMost(Duration.ofSeconds(10))
                .until(() -> latch.getCount() == 0);
    }

    @Test
    void wsQueryForList() throws InterruptedException {
        StompSession session = stompClient
                .connectAsync(url, new StompSessionHandlerAdapter() {
                })
                .join();
        FibonacciOption option = new FibonacciOption(1, 100);
        CountDownLatch latch = new CountDownLatch(1);
        session.subscribe("/user/queue/fibonacci/list", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return List.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, @Nullable Object payload) {
                log.info("Received headers: {} - Payload: {}", headers, payload);
                assertThat(payload).isNotNull();
                latch.countDown();
            }
        });
        session.send("/app/fibonacci/list", option);
        await()
                .atMost(Duration.ofSeconds(5))
                .until(() -> latch.getCount() == 0);
    }

    @Test
    void wsQueryById() throws InterruptedException {
        StompSession session = stompClient
                .connectAsync(url, new StompSessionHandlerAdapter() {
                })
                .join();
        CountDownLatch latch = new CountDownLatch(1);
        session.subscribe("/user/queue/fibonacci/id", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return byte[].class;
            }

            @Override
            public void handleFrame(StompHeaders headers, @Nullable Object payload) {
                log.info("Received headers: {} - Payload: {}", headers, payload);
                assertThat(payload).isNotNull();
                latch.countDown();
            }
        });
        session.send("/app/fibonacci/id", 1);
        await()
                .atMost(Duration.ofSeconds(5))
                .until(() -> latch.getCount() == 0);
    }

    @Test
    void wsQueryByIdFail() throws InterruptedException {
        StompSession session = stompClient
                .connectAsync(url, new StompSessionHandlerAdapter() {
                })
                .join();
        CountDownLatch latch = new CountDownLatch(1);
        session.subscribe("/user/queue/errors", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ProblemDetail.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, @Nullable Object payload) {
                log.info("Received headers: {} - Payload: {}", headers, payload);
                assertThat(payload).isNotNull();
                assertThat(payload).isInstanceOf(ProblemDetail.class);
                latch.countDown();
            }
        });
        session.send("/app/fibonacci/id", 5000);
        await()
                .atMost(Duration.ofSeconds(5))
                .until(() -> latch.getCount() == 0);
    }
}