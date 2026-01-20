package com.github.mangila.fibonacci.sse;

import com.github.mangila.fibonacci.PostgresTestContainerConfiguration;
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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(PostgresTestContainerConfiguration.class)
class SseLivestreamControllerTest {

    private static final Logger log = LoggerFactory.getLogger(SseLivestreamControllerTest.class);

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.webClient = WebClient.create("http://localhost:" + port);
    }

    @Test
    void subscribeLivestream() {
        String channel = "mangila-livestream";
        String streamKey = "livestreamKey";

        Flux<ServerSentEvent<String>> eventStream = webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/api/v1/sse/livestream/{channel}");
                    uriBuilder.queryParam("streamKey", streamKey);
                    return uriBuilder.build(channel);
                })
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.error(new RuntimeException("Error: " + clientResponse.statusCode())))
                .bodyToFlux(new ParameterizedTypeReference<>() {
                });

        StepVerifier.create(eventStream)
                .thenAwait(Duration.ofSeconds(2))
                .assertNext(event -> {
                    log.info("Received event: {}", event);
                    assertThat(event.event()).isEqualTo("livestream");
                    assertThat(event.id()).isEqualTo(streamKey);
                    assertThat(event.data()).isNotNull();
                    assertThat(event.data()).contains("id", "precision");
                })
                .thenCancel()
                .verify(Duration.ofSeconds(5));
    }
}