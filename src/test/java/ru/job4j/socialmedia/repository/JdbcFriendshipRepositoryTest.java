package ru.job4j.socialmedia.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import ru.job4j.socialmedia.entity.FriendshipEntity;
import ru.job4j.socialmedia.exception.SelfFriendshipException;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@SpringBootTest
public class JdbcFriendshipRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17");

    @Autowired
    private FriendshipRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void whenCreateFriendshipRepositoryThenReturnStoredData() {
        var id = UUID.randomUUID();
        var firstUserId = UUID.randomUUID();
        var secondUserId = UUID.randomUUID();
        var offerFriendshipId = insertOfferFriendship(firstUserId, secondUserId);
        var now = Instant.parse("2026-09-22T10:15:00Z");

        var request = new FriendshipRepository.CreateFriendshipRequest(
                id, firstUserId, secondUserId, offerFriendshipId, now
        );

        var result = repository.createFriendship(request);

        var expected = new FriendshipRepository.CreateFriendshipResponse(
                id, firstUserId, secondUserId, offerFriendshipId, now
        );
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void whenCreateFriendshipToSelfThenThrowException() {
        var userId = UUID.randomUUID();
        var userId1 = UUID.randomUUID();
        var offerFriendshipId = insertOfferFriendship(userId, userId1);
        assertThatThrownBy(() -> repository.createFriendship(request(
                UUID.randomUUID(), userId, userId, offerFriendshipId
        ))).isInstanceOf(SelfFriendshipException.class);
    }

    @Test
    void whenFind2FriendshipsThenReturn2Entities() {
        var firstUserId = UUID.randomUUID();
        var secondUserId1 = UUID.randomUUID();
        var secondUserId2 = UUID.randomUUID();

        var offerId1 = insertOfferFriendship(firstUserId, secondUserId1);
        var offerId2 = insertOfferFriendship(firstUserId, secondUserId2);

        var id1 = UUID.randomUUID();
        var id2 = UUID.randomUUID();
        var now = Instant.parse("2026-09-22T10:15:00Z");

        repository.createFriendship(new FriendshipRepository.CreateFriendshipRequest(
                id1, firstUserId, secondUserId1, offerId1, now
        ));
        repository.createFriendship(new FriendshipRepository.CreateFriendshipRequest(
                id2, firstUserId, secondUserId2, offerId2, now
        ));

        var list = repository.findFriendships(firstUserId);

        var expected1 = new FriendshipEntity();
        expected1.setId(id1);
        expected1.setFirstUserId(firstUserId);
        expected1.setSecondUserId(secondUserId1);
        expected1.setOfferFriendshipId(offerId1);
        expected1.setCreatedAt(now);

        var expected2 = new FriendshipEntity();
        expected2.setId(id2);
        expected2.setFirstUserId(firstUserId);
        expected2.setSecondUserId(secondUserId2);
        expected2.setOfferFriendshipId(offerId2);
        expected2.setCreatedAt(now);

        assertThat(list)
                .hasSize(2)
                .containsExactlyInAnyOrder(expected1, expected2);
    }

    @Test
    void whenFindFriendshipsBySecondUserThenReturnEntity() {
        var firstUserId = UUID.randomUUID();
        var secondUserId = UUID.randomUUID();
        var offerId = insertOfferFriendship(firstUserId, secondUserId);
        var friendshipId = UUID.randomUUID();
        var now = Instant.parse("2026-09-22T10:15:00Z");

        repository.createFriendship(new FriendshipRepository.CreateFriendshipRequest(
                friendshipId, firstUserId, secondUserId, offerId, now
        ));

        var list = repository.findFriendships(secondUserId);

        var expected = new FriendshipEntity();
        expected.setId(friendshipId);
        expected.setFirstUserId(firstUserId);
        expected.setSecondUserId(secondUserId);
        expected.setOfferFriendshipId(offerId);
        expected.setCreatedAt(now);

        assertThat(list).containsExactly(expected);
    }

    private FriendshipRepository.CreateFriendshipRequest request(
            UUID id, UUID firstUserId, UUID secondUserId, UUID offerFriendshipId
    ) {
        var now = Instant.parse("2026-09-22T10:15:00Z");
        return new FriendshipRepository.CreateFriendshipRequest(
                id, firstUserId, secondUserId, offerFriendshipId, now
        );
    }

    private UUID insertOfferFriendship(UUID fromUserId, UUID toUserId) {
        var id = UUID.randomUUID();
        jdbcTemplate.update("""
                INSERT INTO offer_friendships (id, from_user_id, to_user_id, status, created_at, updated_at)
                VALUES (?, ?, ?, 'PENDING', now(), now())
                """, id, fromUserId, toUserId);
        return id;
    }
}