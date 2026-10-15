package com.example.rewards.account;

import com.example.rewards.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class EarnPointsIntegrationTest {

    @Autowired
    WebTestClient client;

    @Test
    void earningCreatesAccountAndAccumulatesPoints() {
        String accountId = "acc-" + UUID.randomUUID();

        earn(accountId, 500).expectBody().jsonPath("$.balance").isEqualTo(500);
        earn(accountId, 250).expectBody().jsonPath("$.balance").isEqualTo(750);

        client.get().uri("/accounts/{id}", accountId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accountId").isEqualTo(accountId)
                .jsonPath("$.balance").isEqualTo(750);
    }

    @Test
    void unknownAccountReturns404() {
        client.get().uri("/accounts/{id}", "does-not-exist")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void nonPositivePointsAreRejected() {
        client.post().uri("/accounts/{id}/earn", "acc-" + UUID.randomUUID())
                .bodyValue(Map.of("points", 0))
                .exchange()
                .expectStatus().isBadRequest();
    }

    private WebTestClient.ResponseSpec earn(String accountId, long points) {
        return client.post().uri("/accounts/{id}/earn", accountId)
                .bodyValue(Map.of("points", points))
                .exchange()
                .expectStatus().isOk();
    }
}
