package com.github.mangila.fibonacci.web.sse;

import com.github.mangila.fibonacci.core.model.FibonacciQuery;
import com.github.mangila.fibonacci.web.model.SseSession;
import com.github.mangila.fibonacci.web.service.FibonacciService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebMvcTest(SseController.class)
@Import({RestGlobalErrorHandler.class, RestCustomErrorHandler.class})
public class SseControllerValidationTest {

    private static final Logger log = LoggerFactory.getLogger(SseControllerValidationTest.class);
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FibonacciService service;

    @MockitoBean
    private SseEmitterRegistry emitterRegistry;

    private RestTestClient restTestClient;

    private static final String VALID_CHANNEL = "valid-channel";
    private static final String VALID_STREAM_KEY = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        this.restTestClient = RestTestClient.bindTo(mockMvc)
                .build();
    }

    @ParameterizedTest
    @MethodSource("invalidSubscribeParams")
    void shouldReturnBadRequestWhenSubscribeParamsAreInvalid(String channel, String streamKey) {
        restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/sse/{channel}")
                        .queryParam("streamKey", streamKey)
                        .build(channel))
                .exchange()
                .expectStatus().isBadRequest();
    }

    private static Stream<Arguments> invalidSubscribeParams() {
        return Stream.of(
                Arguments.of("a", VALID_STREAM_KEY), // channel too short
                Arguments.of("invalid_channel", VALID_STREAM_KEY), // invalid character _
                Arguments.of(VALID_CHANNEL, "not-a-uuid"), // invalid UUID
                Arguments.of(VALID_CHANNEL, null) // missing streamKey
        );
    }

    @Test
    void shouldReturnOkWhenSubscribeParamsAreValid() {
        when(emitterRegistry.subscribe(anyString(), anyString()))
                .thenReturn(new SseSession(VALID_CHANNEL, VALID_STREAM_KEY, mock(SseEmitter.class)));

        restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/sse/{channel}")
                        .queryParam("streamKey", VALID_STREAM_KEY)
                        .build(VALID_CHANNEL))
                .exchange()
                .expectStatus()
                .isOk();
    }

    @ParameterizedTest
    @MethodSource("invalidQueryByIdParams")
    void shouldReturnBadRequestWhenQueryByIdParamsAreInvalid(String channel, String streamKey, Object id) {
        restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/sse/{channel}/id")
                        .queryParam("streamKey", streamKey)
                        .queryParam("id", id)
                        .build(channel))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .value(problemDetail -> {
                    log.info("ProblemDetail: {}", problemDetail);
                });
    }

    private static Stream<Arguments> invalidQueryByIdParams() {
        return Stream.of(
                Arguments.of("a", VALID_STREAM_KEY, 1), // channel too short
                Arguments.of(VALID_CHANNEL, "not-a-uuid", 1), // invalid UUID
                Arguments.of(VALID_CHANNEL, VALID_STREAM_KEY, 0), // id < 1
                Arguments.of(VALID_CHANNEL, VALID_STREAM_KEY, -1) // id < 1
        );
    }

    @Test
    void shouldReturnAcceptedWhenQueryByIdParamsAreValid() {
        when(emitterRegistry.getSession(anyString(), anyString()))
                .thenReturn(new SseSession(VALID_CHANNEL, VALID_STREAM_KEY, mock(SseEmitter.class)));

        restTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/sse/{channel}/id")
                        .queryParam("streamKey", VALID_STREAM_KEY)
                        .queryParam("id", 1)
                        .build(VALID_CHANNEL))
                .exchange()
                .expectStatus().isAccepted();
    }

    @ParameterizedTest
    @MethodSource("invalidQueryForListParams")
    void shouldReturnBadRequestWhenQueryForListParamsAreInvalid(String channel, String streamKey, FibonacciQuery query) {
        restTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/sse/{channel}/list")
                        .queryParam("streamKey", streamKey)
                        .build(channel))
                .contentType(MediaType.APPLICATION_JSON)
                .body(query)
                .exchange()
                .expectStatus().isBadRequest();
    }

    private static Stream<Arguments> invalidQueryForListParams() {
        return Stream.of(
                Arguments.of("a", VALID_STREAM_KEY, new FibonacciQuery(0, 10)), // channel too short
                Arguments.of(VALID_CHANNEL, "not-a-uuid", new FibonacciQuery(0, 10)), // invalid UUID
                Arguments.of(VALID_CHANNEL, VALID_STREAM_KEY, new FibonacciQuery(-1, 10)), // offset < 0
                Arguments.of(VALID_CHANNEL, VALID_STREAM_KEY, new FibonacciQuery(0, 0)), // limit < 1
                Arguments.of(VALID_CHANNEL, VALID_STREAM_KEY, new FibonacciQuery(0, 1001)) // limit > 1000
        );
    }

    @Test
    void shouldReturnAcceptedWhenQueryForListParamsAreValid() {
        when(emitterRegistry.getSession(anyString(), anyString()))
                .thenReturn(new SseSession(VALID_CHANNEL, VALID_STREAM_KEY, mock(SseEmitter.class)));

        restTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/sse/{channel}/list")
                        .queryParam("streamKey", VALID_STREAM_KEY)
                        .build(VALID_CHANNEL))
                .contentType(MediaType.APPLICATION_JSON)
                .body(new FibonacciQuery(0, 10))
                .exchange()
                .expectStatus().isAccepted();
    }
}
