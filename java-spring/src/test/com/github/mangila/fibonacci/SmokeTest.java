package com.github.mangila.fibonacci;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(PostgresTestContainerConfiguration.class)
public class SmokeTest {

    @Test
    void contextLoads() {
    }

}
