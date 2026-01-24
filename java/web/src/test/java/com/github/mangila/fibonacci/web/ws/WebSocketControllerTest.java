package com.github.mangila.fibonacci.web.ws;

import com.github.mangila.fibonacci.web.PostgresTestContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@PostgresTestContainer
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketControllerTest {

    @LocalServerPort
    private int port;

}