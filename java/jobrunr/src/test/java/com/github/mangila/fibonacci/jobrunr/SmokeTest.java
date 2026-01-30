package com.github.mangila.fibonacci.jobrunr;

import com.github.mangila.fibonacci.postgres.test.PostgresTestContainer;
import com.github.mangila.fibonacci.redis.test.RedisTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@RedisTestContainer
@PostgresTestContainer
@SpringBootTest
class SmokeTest {

    @Test
    void contextLoads() {
    }

}
