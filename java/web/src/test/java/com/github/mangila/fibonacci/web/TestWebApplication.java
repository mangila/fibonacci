package com.github.mangila.fibonacci.web;

import org.springframework.boot.SpringApplication;

public class TestWebApplication {

    static void main(String[] args) {
        SpringApplication.from(WebApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
