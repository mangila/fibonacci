package com.github.mangila.fibonacci.web.sse;

import com.github.mangila.fibonacci.service.FibonacciService;
import com.github.mangila.fibonacci.web.ErrorHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;

import java.util.UUID;

@WebMvcTest(SseController.class)
public class SseControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FibonacciService service;

    @MockitoBean
    private SseEmitterRegistry emitterRegistry;

    @MockitoSpyBean
    private ErrorHandler errorHandler;

    private WebTestClient webTestClient;

    @BeforeEach
    void beforeEach() {
        this.webTestClient = MockMvcWebTestClient.bindTo(mockMvc)
                .build();
    }

    @Test
    void notValidChannelName() {
        String channel = "fail-channel-åäö";
        String streamKey = UUID.randomUUID().toString();
        webTestClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/api/v1/sse/{channel}");
                    uriBuilder.queryParam("streamKey", streamKey);
                    return uriBuilder.build(channel);
                })
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void notValidStreamKey() {
        String channel = "valid-channel";
        String streamKey = "fail-key";
        webTestClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/api/v1/sse/{channel}");
                    uriBuilder.queryParam("streamKey", streamKey);
                    return uriBuilder.build(channel);
                })
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void notValidId() {
        String channel = "valid-channel";
        String streamKey = UUID.randomUUID().toString();
        int id = -1;
        webTestClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/api/v1/sse/{channel}/id");
                    uriBuilder.queryParam("streamKey", streamKey);
                    uriBuilder.queryParam("id", id);
                    return uriBuilder.build(channel);
                })
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void notValidFibonacciOption() {
        String channel = "valid-channel";
        String streamKey = UUID.randomUUID().toString();
        // language=JSON
        String json = """
                {
                    "offset": -1,
                    "limit": 10
                }
                """;
        webTestClient.post()
                .uri(uriBuilder -> {
                    uriBuilder.path("/api/v1/sse/{channel}/list");
                    uriBuilder.queryParam("streamKey", streamKey);
                    return uriBuilder.build(channel);
                })
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest();

        // language=JSON
        json = """
                {
                    "offset": 1,
                    "limit": -1
                }
                """;
        webTestClient.post()
                .uri(uriBuilder -> {
                    uriBuilder.path("/api/v1/sse/{channel}/list");
                    uriBuilder.queryParam("streamKey", streamKey);
                    return uriBuilder.build(channel);
                })
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
