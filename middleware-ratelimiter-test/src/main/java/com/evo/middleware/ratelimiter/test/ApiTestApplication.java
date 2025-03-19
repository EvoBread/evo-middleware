package com.evo.middleware.ratelimiter.test;

import com.evo.middleware.ratelimiter.annotation.EnableEvoRateLimiter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEvoRateLimiter
@ComponentScan("com.evo.*")
public class ApiTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiTestApplication.class, args);
    }
}
