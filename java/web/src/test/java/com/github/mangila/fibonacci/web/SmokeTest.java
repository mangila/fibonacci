package com.github.mangila.fibonacci.web;

import com.github.mangila.fibonacci.postgres.test.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@PostgresTestContainer
@SpringBootTest
class SmokeTest {

    @Test
    void contextLoads() {
    }

}
