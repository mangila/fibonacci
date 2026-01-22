package com.github.mangila.fibonacci.web.sse;

import com.github.mangila.fibonacci.PostgresTestContainerConfiguration;
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
                "app.livestream.enabled=true",
                "app.sse.heartbeat.enabled=false"
        })
@Import(PostgresTestContainerConfiguration.class)
public class SseControllerLivestreamTest {

    private static final Logger log = LoggerFactory.getLogger(SseControllerLivestreamTest.class);

    @LocalServerPort
    private int port;

    private WebClient webClient;

    String channel = "mangila-sse-livestream";
    String streamKey = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        this.webClient = WebClient.create("http://localhost:" + port);
    }

    @Test
    void sseLiveStream() {
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
                .thenConsumeWhile(stringServerSentEvent -> {
                    return !stringServerSentEvent.event().equals("livestream");
                })
                .assertNext(event -> {
                    log.info("Received event: {}", event);
                    assertThatJson(event.data())
                            .as("Fibonacci SSE event data livestream assertion")
                            .isArray()
                            .isNotEmpty()
                            .hasSizeLessThan(11)
                            .element(0)
                            .isObject()
                            .containsOnlyKeys("id", "sequence", "precision");
                    assertThat(event.event())
                            .as("Fibonacci SSE event name query livestream assertion")
                            .isEqualTo("livestream");
                    assertThat(event.id())
                            .as("Fibonacci SSE event streamKey query livestream assertion")
                            .isEqualTo(streamKey);
                })
                .thenCancel()
                .verify(Duration.ofSeconds(5));
    }
}
