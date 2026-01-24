package com.github.mangila.fibonacci.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(PostgresTestContainerConfiguration.class)
@SpringBootTest
class SmokeTest {

    @Test
    void contextLoads() {
    }

}
