package ru.job4j.socialmedia.e2e;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.job4j.socialmedia.controller.dto.CreateOfferFriendshipRequest;
import ru.job4j.socialmedia.controller.dto.OfferFriendshipResponse;
import ru.job4j.socialmedia.controller.dto.RejectOfferFriendshipRequest;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static ru.job4j.socialmedia.controller.dto.OfferFriendshipResponse.StatusEnum.PENDING;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class FriendshipApiE2ETest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    RestTestClient restClient;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM offer_friendships");
    }

    @Test
    void whenCreateOfferFriendshipThenOfferIsSaved() {
        var fromUserId = UUID.fromString("77926b15-870c-4366-93e3-6ac74b61b894");
        var toUserId = UUID.fromString("fd9e3b80-9602-427f-87ec-5763c4afbcd8");
        var request = new CreateOfferFriendshipRequest()
                .fromUserId(fromUserId)
                .toUserId(toUserId);

        var response = restClient
                .post()
                .uri("/api/offer-friendships/create")
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(OfferFriendshipResponse.class);

        assertThat(response.getStatus().value()).isEqualTo(HttpStatus.CREATED.value());

        var responseDto = response.getResponseBody();
        var expected = new OfferFriendshipResponse()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .status(PENDING);

        assertThat(responseDto)
                .usingRecursiveComparison()
                .ignoringFields("offerId", "createdAt", "updatedAt")
                .isEqualTo(expected);
    }

    @Test
    void whenCreateOfferFriendshipThenOfferExistExeption204() {
        var fromUserId = UUID.fromString("77926b15-870c-4366-93e3-6ac74b61b894");
        var toUserId = UUID.fromString("fd9e3b80-9602-427f-87ec-5763c4afbcd8");
        var request = new CreateOfferFriendshipRequest()
                .fromUserId(fromUserId)
                .toUserId(toUserId);

        var requestRepeat = new CreateOfferFriendshipRequest()
                .fromUserId(fromUserId)
                .toUserId(toUserId);

        var response = restClient
                .post()
                .uri("/api/offer-friendships/create")
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(OfferFriendshipResponse.class);

        var responseRepeat = restClient
                .post()
                .uri("/api/offer-friendships/create")
                .body(requestRepeat)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void whenCreateOfferFriendshipThenBadRequestExeption400() {
        var fromUserId = UUID.fromString("77926b15-870c-4366-93e3-6ac74b61b894");
        var request = new CreateOfferFriendshipRequest()
                .fromUserId(fromUserId);

        var response = restClient
                .post()
                .uri("/api/offer-friendships/create")
                .body(request)
                .exchange()
                .expectStatus().isBadRequest()
                .returnResult(ProblemDetail.class);
        assertThat(response.getResponseBody()).isNotNull();
        assertThat(response.getResponseBody().getStatus()).isEqualTo(400);
    }

    @Test
    void whenCreateOfferFriendshipWithExistingRejectedThen409() {
        var fromUserId = UUID.fromString("77926b15-870c-4366-93e3-6ac74b61b894");
        var toUserId = UUID.fromString("fd9e3b80-9602-427f-87ec-5763c4afbcd8");
        var request = new CreateOfferFriendshipRequest()
                .fromUserId(fromUserId)
                .toUserId(toUserId);

        // 1. Создать заявку (PENDING) → 201
        var created = restClient
                .post()
                .uri("/api/offer-friendships/create")
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(OfferFriendshipResponse.class)
                .getResponseBody();

        assertThat(created).isNotNull();
        var offerId = created.getOfferId();

        // 2. Отклонить заявку (PENDING → REJECTED) → 200/204
        var rejectRequest = new RejectOfferFriendshipRequest()
                .offerId(offerId);
        restClient
                .post()
                .uri("/api/offer-friendships/reject")
                .body(rejectRequest)
                .exchange()
                .expectStatus().is2xxSuccessful();   // 200 или 204 — зависит от вашей реализации

        // 3. Повторно создать — заявка уже REJECTED → 409
        var response = restClient
                .post()
                .uri("/api/offer-friendships/create")
                .body(request)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .returnResult(ProblemDetail.class);

        var problem = response.getResponseBody();
        assertThat(problem).isNotNull();
        assertThat(problem.getStatus()).isEqualTo(409);
        assertThat(problem.getTitle()).isEqualTo("Friendship wrong status");
        assertThat(problem.getDetail()).contains("REJECTED");
    }
}

