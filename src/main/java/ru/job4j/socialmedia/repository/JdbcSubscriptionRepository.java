package ru.job4j.socialmedia.repository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.job4j.socialmedia.exception.SelfSubscriptionException;

import java.util.List;
import java.util.UUID;

@Repository
public class JdbcSubscriptionRepository implements SubscriptionRepository {


    private static final String CREATE = """
            INSERT INTO subscriptions (
                id, follower_id, followed_id, created_at
            )
            VALUES (?, ?, ?, ?)
            RETURNING id, follower_id, followed_id, created_at
            """;

    private static final String ALLSUBSCRIBE = """
            SELECT followed_id
            FROM subscriptions
            WHERE follower_id = ?
            ORDER BY created_at DESC, id DESC
            """;

    private static final String ALLFALLOWERS = """
            SELECT follower_id
            FROM subscriptions
            WHERE followed_id = ?
            ORDER BY created_at DESC, id DESC
            """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcSubscriptionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CreateSubscriptionResponse createSubscription(CreateSubscriptionRequest request) {
        try {
            return jdbcTemplate.queryForObject(
                    CREATE,
                    (resultSet, rowNum) -> new CreateSubscriptionResponse(
                            resultSet.getObject("id", java.util.UUID.class),
                            resultSet.getObject("follower_id", java.util.UUID.class),
                            resultSet.getObject("followed_id", java.util.UUID.class),
                            resultSet.getTimestamp("created_at").toInstant()
                    ),
                    request.id(),
                    request.followerId(),
                    request.followedId(),
                    java.sql.Timestamp.from(request.createdAt())
            );
        } catch (DataIntegrityViolationException e) {
            var message = e.getMostSpecificCause().getMessage();
            if (message != null && message.contains("chk_subscription_users")) {
                throw new SelfSubscriptionException(message);
            }
            throw e;
        }
    }

    @Override
    public List<UUID> findSubscriptions(UUID userId) {
        return jdbcTemplate.query(
                ALLSUBSCRIBE,
                (resultSet, rowNum) -> resultSet.getObject("followed_id", UUID.class),
                userId
        );
    }

    @Override
    public List<UUID> findFollowers(UUID userId) {
        return jdbcTemplate.query(
                ALLFALLOWERS,
                (resultSet, rowNum) -> resultSet.getObject("follower_id", UUID.class),
                userId
        );
    }
}
