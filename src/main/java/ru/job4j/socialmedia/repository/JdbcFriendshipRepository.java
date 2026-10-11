package ru.job4j.socialmedia.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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

    private final JdbcTemplate jdbcTemplate;

    public JdbcFriendshipRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public FriendshipRepository.CreateFriendshipResponse createFriendship(FriendshipRepository.CreateFriendshipRequest request) {
        return jdbcTemplate.queryForObject(
                CREATE,
                (resultSet, rowNum) -> new FriendshipRepository.CreateFriendshipResponse(
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
    }

}
