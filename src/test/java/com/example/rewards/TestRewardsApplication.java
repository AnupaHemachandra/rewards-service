package com.example.rewards;

import org.springframework.boot.SpringApplication;

/**
 * Runs the app locally against a throwaway PostgreSQL container.
 * Start with: mvn spring-boot:test-run   (or run this class from the IDE)
 * The API is then on http://localhost:8080
 */
public class TestRewardsApplication {

    public static void main(String[] args) {
        SpringApplication.from(RewardsApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
