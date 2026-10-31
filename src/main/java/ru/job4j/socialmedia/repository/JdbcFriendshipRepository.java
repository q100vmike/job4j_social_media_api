package ru.job4j.socialmedia.repository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.job4j.socialmedia.entity.FriendshipEntity;
import ru.job4j.socialmedia.exception.SelfFriendshipException;
import org.springframework.jdbc.core.RowMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class JdbcFriendshipRepository implements FriendshipRepository {

    private static final String CREATE = """
            INSERT INTO friendships (
                id, first_user_id, second_user_id,
                offer_friendship_id, created_at
            )
            VALUES (?, ?, ?, ?, ?)
            RETURNING id, first_user_id, second_user_id,
                offer_friendship_id, created_at
            """;

    private static final String ALLFRIENDS = """
        SELECT id, first_user_id, second_user_id,
               offer_friendship_id, created_at
        FROM friendships
        WHERE first_user_id = ? OR second_user_id = ?
        ORDER BY created_at DESC, id DESC
        """;

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<FriendshipEntity> FRIENDSHIP_ROW_MAPPER = (rs, rowNum) -> {
        FriendshipEntity friendship = new FriendshipEntity();
        friendship.setId(rs.getObject("id", UUID.class));
        friendship.setFirstUserId(rs.getObject("first_user_id", UUID.class));
        friendship.setSecondUserId(rs.getObject("second_user_id", UUID.class));
        friendship.setOfferFriendshipId(rs.getObject("offer_friendship_id", UUID.class));
        friendship.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class).toInstant());
        return friendship;
    };

    public JdbcFriendshipRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CreateFriendshipResponse createFriendship(CreateFriendshipRequest request) {
        try {
            return jdbcTemplate.queryForObject(
                    CREATE,
                    (resultSet, rowNum) -> new CreateFriendshipResponse(
                            resultSet.getObject("id", java.util.UUID.class),
                            resultSet.getObject("first_user_id", java.util.UUID.class),
                            resultSet.getObject("second_user_id", java.util.UUID.class),
                            resultSet.getObject("offer_friendship_id", java.util.UUID.class),
                            resultSet.getTimestamp("created_at").toInstant()
                    ),
                    request.id(),
                    request.firstUserId(),
                    request.secondUserId(),
                    request.offerFriendshipId(),
                    java.sql.Timestamp.from(request.createdAt())
            );
        } catch (DataIntegrityViolationException e) {
            var message = e.getMostSpecificCause().getMessage();
            if (message != null && message.contains("chk_friendship_users")) {
                throw new SelfFriendshipException(message);
            }
            throw e;
        }
    }

    @Override
    public List<FriendshipEntity> findFriendships(UUID userId) {
        return jdbcTemplate.query(ALLFRIENDS, FRIENDSHIP_ROW_MAPPER, userId, userId);
    }

}
