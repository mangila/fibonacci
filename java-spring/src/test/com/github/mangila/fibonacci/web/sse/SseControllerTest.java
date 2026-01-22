package com.github.mangila.fibonacci.web.sse;

import com.github.mangila.fibonacci.PostgresTestContainerConfiguration;
import com.github.mangila.fibonacci.web.dto.FibonacciQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "app.fibonacci.algorithm=iterative",
                "app.fibonacci.offset=1",
                "app.fibonacci.limit=1000",
                "app.fibonacci.delay=100ms",
                "app.livestream.enabled=false",
                "app.sse.heartbeat.enabled=false"
        })
@Import(PostgresTestContainerConfiguration.class)
class SseControllerTest {

    private static final Logger log = LoggerFactory.getLogger(SseControllerTest.class);

    @LocalServerPort
    private int port;

    private WebClient webClient;

    String channel = "mangila-sse-stream";
    String streamKey = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        this.webClient = WebClient.create("http://localhost:" + port);
    }

    @Test
    void sseQueryForList() {
        Flux<ServerSentEvent<String>> eventStream = webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/api/v1/sse/{channel}");
                    uriBuilder.queryParam("streamKey", streamKey);
                    return uriBuilder.build(channel);
                })
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(new RuntimeException("Error: " + clientResponse.statusCode())))
                .bodyToFlux(new ParameterizedTypeReference<>() {
                });

        StepVerifier.create(eventStream)
                .thenAwait(Duration.ofMillis(500))
                .then(() -> webClient.post()
                        .uri(uriBuilder -> {
                            uriBuilder.path("/api/v1/sse/{channel}/list");
                            uriBuilder.queryParam("streamKey", streamKey);
                            return uriBuilder.build(channel);
                        })
                        .bodyValue(new FibonacciQuery(1, 10))
                        .retrieve()
                        .toBodilessEntity()
                        .subscribe())
                .thenConsumeWhile(stringServerSentEvent -> {
                    return !stringServerSentEvent.event().equals("list");
                })
                .assertNext(event -> {
                    log.info("Received event: {}", event);
                    assertThatJson(event.data())
                            .as("Fibonacci SSE event query list assertion")
                            .isArray()
                            .isNotEmpty()
                            .hasSizeLessThan(11)
                            .element(0)
                            .isObject()
                            .containsOnlyKeys("id", "sequence", "precision");
                    assertThat(event.event())
                            .as("Fibonacci SSE event name query list assertion")
                            .isEqualTo("list");
                    assertThat(event.id())
                            .as("Fibonacci SSE event streamKey query list assertion")
                            .isEqualTo(streamKey);
                })
                .thenCancel()
                .verify(Duration.ofSeconds(10));
    }

    @Test
    void sseQueryById() {
        Flux<ServerSentEvent<String>> eventStream = webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/api/v1/sse/{channel}");
                    uriBuilder.queryParam("streamKey", streamKey);
                    return uriBuilder.build(channel);
                })
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(new RuntimeException("Error: " + clientResponse.statusCode())))
                .bodyToFlux(new ParameterizedTypeReference<>() {
                });

        StepVerifier.create(eventStream)
                .thenAwait(Duration.ofMillis(500))
                .then(() -> webClient.get()
                        .uri(uriBuilder -> {
                            uriBuilder.path("/api/v1/sse/{channel}/id");
                            uriBuilder.queryParam("streamKey", streamKey);
                            uriBuilder.queryParam("id", 1);
                            return uriBuilder.build(channel);
                        })
                        .retrieve()
                        .toBodilessEntity()
                        .subscribe())
                .thenConsumeWhile(stringServerSentEvent -> {
                    return !stringServerSentEvent.event().equals("id");
                })
                .assertNext(event -> {
                    log.info("Received event: {}", event);
                    assertThatJson(event.data())
                            .as("Fibonacci SSE event data id query assertion")
                            .isObject()
                            .containsOnlyKeys("id", "sequence", "result", "precision");
                    assertThat(event.event())
                            .as("Fibonacci SSE event name id query assertion")
                            .isEqualTo("id");
                    assertThat(event.id())
                            .as("Fibonacci SSE event streamKey id query assertion")
                            .isEqualTo(streamKey);
                })
                .thenCancel()
                .verify(Duration.ofSeconds(10));
    }
}