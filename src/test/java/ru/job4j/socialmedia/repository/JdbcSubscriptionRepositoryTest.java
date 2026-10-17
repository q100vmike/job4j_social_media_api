package ru.job4j.socialmedia.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import ru.job4j.socialmedia.exception.SelfSubscriptionException;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@SpringBootTest
public class JdbcSubscriptionRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17");

    @Autowired
    private SubscriptionRepository repository;

    @Test
    void whenCreateSubscriptionRepositoryThenReturnStoredData() {
        var id = UUID.randomUUID();
        var followerId = UUID.randomUUID();
        var followedId = UUID.randomUUID();
        var now = Instant.parse("2026-09-22T10:15:00Z");
        var request = new SubscriptionRepository.CreateSubscriptionRequest(
                id,
                followerId,
                followedId,
                now
        );

        var result = repository.createSubscription(request);

        var expected = new SubscriptionRepository
                .CreateSubscriptionResponse(
                id,
                followerId,
                followedId,
                now
        );
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void whenCreateDuplicateSubscriptionRepositoryThenThrowException() {
        var followerId = UUID.randomUUID();

        assertThatThrownBy(() -> repository.createSubscription(request(
                UUID.randomUUID(), followerId, followerId
        ))).isInstanceOf(SelfSubscriptionException.class);
    }

    @Test
    void whenFind2findSubscriptions(){
        var id = UUID.randomUUID();
        var followerId = UUID.randomUUID();
        var followedId = UUID.randomUUID();
        var id2 = UUID.randomUUID();
        var followedId2 = UUID.randomUUID();
        var now = Instant.parse("2026-09-22T10:15:00Z");
        var request = new SubscriptionRepository.CreateSubscriptionRequest(
                id,
                followerId,
                followedId,
                now
        );
        var request2 = new SubscriptionRepository.CreateSubscriptionRequest(
                id2,
                followerId,
                followedId2,
                now
        );

        repository.createSubscription(request);
        repository.createSubscription(request2);

        var list = repository.findSubscriptions(followerId);
        assertThat(list)
                .hasSize(2)
                .containsExactlyInAnyOrder(followedId, followedId2);

    }


    @Test
    void whenFind2findFollowers(){
        var id = UUID.randomUUID();
        var followerId = UUID.randomUUID();
        var followedId = UUID.randomUUID();
        var id2 = UUID.randomUUID();
        var followerId2 = UUID.randomUUID();
        var now = Instant.parse("2026-09-22T10:15:00Z");
        var request = new SubscriptionRepository.CreateSubscriptionRequest(
                id,
                followerId,
                followedId,
                now
        );
        var request2 = new SubscriptionRepository.CreateSubscriptionRequest(
                id2,
                followerId2,
                followedId,
                now
        );

        repository.createSubscription(request);
        repository.createSubscription(request2);

        var list = repository.findFollowers(followedId);
        assertThat(list)
                .hasSize(2)
                .containsExactlyInAnyOrder(followerId, followerId2);

    }

    private SubscriptionRepository.CreateSubscriptionRequest request(
            UUID id,
            UUID followerId,
            UUID followedId
    ) {
        var now = Instant.parse("2026-09-22T10:15:00Z");
        return new SubscriptionRepository.CreateSubscriptionRequest(
                id, followerId, followedId, now
        );
    }
}
