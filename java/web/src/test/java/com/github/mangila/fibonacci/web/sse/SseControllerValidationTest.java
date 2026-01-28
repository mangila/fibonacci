package com.github.mangila.fibonacci.web.sse;

import com.github.mangila.fibonacci.web.repository.FibonacciRepository;
import com.github.mangila.fibonacci.web.dto.SseSubscription;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.UUID;

@WebMvcTest(SseController.class)
@Import({RestGlobalErrorHandler.class, RestCustomErrorHandler.class})
public class SseControllerValidationTest {

    private static final Logger log = LoggerFactory.getLogger(SseControllerValidationTest.class);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FibonacciRepository repository;

    @MockitoBean
    private SseEmitterRegistry emitterRegistry;

    private RestTestClient restTestClient;

    private static final SseSubscription TEST_SUBSCRIPTION = new SseSubscription(
            "valid-channel",
            UUID.randomUUID().toString()
    );

    @BeforeEach
    void setUp() {
        this.restTestClient = RestTestClient.bindTo(mockMvc)
                .build();
    }
}
