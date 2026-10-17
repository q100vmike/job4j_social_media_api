package ru.job4j.socialmedia.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import ru.job4j.socialmedia.exception.OfferFriendshipAlreadyExistsException;
import ru.job4j.socialmedia.exception.SelfFriendshipException;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.job4j.socialmedia.repository.OfferFriendshipRepository.Status.PENDING;

@Testcontainers
@SpringBootTest
class JdbcOfferFriendshipRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17");

    @Autowired
    private OfferFriendshipRepository repository;

    @Test
    void whenCreateOfferFriendshipThenReturnStoredData() {
        var id = UUID.randomUUID();
        var fromUserId = UUID.randomUUID();
        var toUserId = UUID.randomUUID();
        var now = Instant.parse("2026-09-22T10:15:00Z");
        var request = new OfferFriendshipRepository.CreateOfferFriendshipRequest(
                id,
                fromUserId,
                toUserId,
                PENDING,
                now,
                now
        );

        var result = repository.createOfferFriendship(request);

        var expected = new OfferFriendshipRepository
                .CreateOfferFriendshipResponse(
                id,
                fromUserId,
                toUserId,
                PENDING,
                now,
                now
        );
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void whenFind2IncomingOfferFriendships(){
        var id = UUID.randomUUID();
        var fromUserId = UUID.randomUUID();
        var toUserId = UUID.randomUUID();
        var id2 = UUID.randomUUID();
        var fromUserId2 = UUID.randomUUID();
        var now = Instant.parse("2026-09-22T10:15:00Z");
        var request = new OfferFriendshipRepository.CreateOfferFriendshipRequest(
                id,
                fromUserId,
                toUserId,
                PENDING,
                now,
                now
        );
        var request2 = new OfferFriendshipRepository.CreateOfferFriendshipRequest(
                id2,
                fromUserId2,
                toUserId,
                PENDING,
                now,
                now
        );

        repository.createOfferFriendship(request);
        repository.createOfferFriendship(request2);

        var list = repository.findIncomingOfferFriendships(toUserId);
        assertThat(list)
                .hasSize(2)
                .containsExactlyInAnyOrder(fromUserId, fromUserId2);

    }

    @Test
    void whenFind2OutOfferFriendships(){
        var id = UUID.randomUUID();
        var fromUserId = UUID.randomUUID();
        var toUserId = UUID.randomUUID();
        var id2 = UUID.randomUUID();
        var toUserId2 = UUID.randomUUID();
        var now = Instant.parse("2026-09-22T10:15:00Z");
        var request = new OfferFriendshipRepository.CreateOfferFriendshipRequest(
                id,
                fromUserId,
                toUserId,
                PENDING,
                now,
                now
        );
        var request2 = new OfferFriendshipRepository.CreateOfferFriendshipRequest(
                id2,
                fromUserId,
                toUserId2,
                PENDING,
                now,
                now
        );

        repository.createOfferFriendship(request);
        repository.createOfferFriendship(request2);

        var list = repository.findOutgoingOfferFriendships(fromUserId);
        assertThat(list)
                .hasSize(2)
                .containsExactlyInAnyOrder(toUserId, toUserId2);

    }

    @Test
    void whenCreateDuplicateOfferFriendshipThenThrowException() {
        var fromUserId = UUID.randomUUID();
        var toUserId = UUID.randomUUID();
        repository.createOfferFriendship(request(
                UUID.randomUUID(), fromUserId, toUserId, PENDING
        ));

        assertThatThrownBy(() -> repository.createOfferFriendship(request(
                UUID.randomUUID(), fromUserId, toUserId, PENDING
        ))).isInstanceOf(OfferFriendshipAlreadyExistsException.class);
    }

    @Test
    void whenCreateOfferFriendshipToSelfThenThrowException() {
        var userId = UUID.randomUUID();

        assertThatThrownBy(() -> repository.createOfferFriendship(request(
                UUID.randomUUID(), userId, userId, PENDING
        ))).isInstanceOf(SelfFriendshipException.class);
    }

    @Test
    @Disabled("Тест отключён: теряет смысл если значение status стало перечислимым типом")
    void whenCreateOfferFriendshipWithUnknownStatusThenThrowException() {
        assertThatThrownBy(() -> repository.createOfferFriendship(request(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                OfferFriendshipRepository.Status.valueOf("UNKNOWN")
        ))).isInstanceOf(DataIntegrityViolationException.class);
    }

    private OfferFriendshipRepository.CreateOfferFriendshipRequest request(
            UUID id,
            UUID fromUserId,
            UUID toUserId,
            OfferFriendshipRepository.Status status
    ) {
        var now = Instant.parse("2026-09-22T10:15:00Z");
        return new OfferFriendshipRepository.CreateOfferFriendshipRequest(
                id, fromUserId, toUserId, status, now, now
        );
    }

}
