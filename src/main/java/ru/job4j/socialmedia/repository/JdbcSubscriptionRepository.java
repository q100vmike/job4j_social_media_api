package ru.job4j.socialmedia.repository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.job4j.socialmedia.entity.SubscriptionEntity;
import ru.job4j.socialmedia.exception.SelfSubscriptionException;

import java.time.OffsetDateTime;
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
            SELECT id, followed_id, follower_id, created_at
            FROM subscriptions
            WHERE follower_id = ?
            ORDER BY created_at DESC, id DESC
            """;

    private static final String ALLFOLLOWERS = """
            SELECT id, followed_id, follower_id, created_at
            FROM subscriptions
            WHERE followed_id = ?
            ORDER BY created_at DESC, id DESC
            """;

    private static final RowMapper<SubscriptionEntity> SUBSCRIPTION_ROW_MAPPER = (rs, rowNum) -> {
        SubscriptionEntity subscription = new SubscriptionEntity();
        subscription.setId(rs.getObject("id", UUID.class));
        subscription.setFollowerId(rs.getObject("follower_id", UUID.class));
        subscription.setFollowedId(rs.getObject("followed_id", UUID.class));
        subscription.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class).toInstant());
        return subscription;
    };

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
                            resultSet.getObject("id", UUID.class),
                            resultSet.getObject("follower_id", UUID.class),
                            resultSet.getObject("followed_id", UUID.class),
                            resultSet.getObject("created_at", OffsetDateTime.class).toInstant()
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
    public List<SubscriptionEntity> findSubscriptions(UUID userId) {
        return jdbcTemplate.query(ALLSUBSCRIBE, SUBSCRIPTION_ROW_MAPPER, userId);
    }

    @Override
    public List<SubscriptionEntity> findFollowers(UUID userId) {
        return jdbcTemplate.query(ALLFOLLOWERS, SUBSCRIPTION_ROW_MAPPER, userId);
    }
}