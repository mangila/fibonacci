package com.github.mangila.fibonacci.sse;

import com.github.mangila.fibonacci.PostgresTestContainerConfiguration;
import com.github.mangila.fibonacci.model.FibonacciOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgresTestContainerConfiguration.class)
class SseControllerTest {

    private static final Logger log = LoggerFactory.getLogger(SseControllerTest.class);

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;

    private WebClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.webClient = WebClient.create("http://localhost:" + port);
        this.webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
    }

    @Test
    void sseLivestream() {
        String username = "user-livestream";

        Flux<ServerSentEvent<String>> eventStream = webClient.get()
                .uri("api/v1/sse/fibonacci/subscribe/{username}", username)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(new RuntimeException("Error: " + clientResponse.statusCode())))
                .bodyToFlux(new ParameterizedTypeReference<>() {
                });

        StepVerifier.create(eventStream)
                .thenAwait(Duration.ofSeconds(2))
                .then(() -> webTestClient.get()
                        .uri("api/v1/sse/fibonacci/subscribe/livestream/{username}", username)
                        .exchange()
                        .expectStatus()
                        .is2xxSuccessful())
                .assertNext(event -> {
                    log.info("Received event: {}", event);
                    assertThat(event.event()).isEqualTo("livestream");
                    assertThat(event.id()).isEqualTo(username);
                    assertThat(event.data()).isNotNull();
                    assertThat(event.data()).contains("id", "precision");
                })
                .thenCancel()
                .verify(Duration.ofSeconds(5));
        unsubscribe(username);
    }

    @Test
    void subscribeAndQueryForList() {
        String username = "user-list";

        Flux<ServerSentEvent<String>> eventStream = webClient.get()
                .uri("/api/v1/sse/fibonacci/subscribe/{username}", username)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<>() {
                });

        StepVerifier.create(eventStream)
                .thenAwait(Duration.ofSeconds(2))
                .then(() -> webClient.post()
                        .uri("/api/v1/sse/fibonacci/{username}", username)
                        .bodyValue(new FibonacciOption(0, 1))
                        .retrieve()
                        .toBodilessEntity()
                        .subscribe())
                .assertNext(event -> {
                    log.info("Received event: {}", event);
                    assertThat(event.event()).isEqualTo("list");
                    assertThat(event.id()).isEqualTo(username);
                    assertThat(event.data()).isNotNull();
                    assertThat(event.data()).contains("id", "result", "precision");
                })
                .thenCancel()
                .verify(Duration.ofSeconds(10));
        unsubscribe(username);
    }

    @Test
    void queryById() {
        String username = "user-id";

        Flux<ServerSentEvent<String>> eventStream = webClient.get()
                .uri("/api/v1/sse/fibonacci/subscribe/{username}", username)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<>() {
                });

        StepVerifier.create(eventStream)
                .thenAwait(Duration.ofSeconds(2))
                .then(() -> webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/api/v1/sse/fibonacci/{streamKey}")
                                .queryParam("id", 1)
                                .build(username))
                        .retrieve()
                        .toBodilessEntity()
                        .subscribe())
                .thenConsumeWhile(event -> {
                    return !event.event().equals("id");
                })
                .assertNext(event -> {
                    log.info("Received event: {}", event);
                    assertThat(event.event()).isEqualTo("id");
                    assertThat(event.id()).isEqualTo(username);
                    assertThat(event.data()).isNotNull();
                    assertThat(event.data()).contains("id", "result", "precision");
                })
                .thenCancel()
                .verify(Duration.ofSeconds(10));
        unsubscribe(username);
    }

    void unsubscribe(String username) {
        webClient.delete()
                .uri("/api/v1/sse/fibonacci/subscribe/{username}", username)
                .retrieve()
                .toBodilessEntity()
                .block(Duration.ofSeconds(5));
    }
}