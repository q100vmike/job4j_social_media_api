package ru.job4j.socialmedia.repository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.job4j.socialmedia.entity.OfferFriendshipEntity;
import ru.job4j.socialmedia.exception.OfferFriendshipAlreadyExistsException;
import ru.job4j.socialmedia.exception.SelfFriendshipException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class JdbcOfferFriendshipRepository
        implements OfferFriendshipRepository {

    private static final String CREATE = """
            INSERT INTO offer_friendships (
                id, from_user_id, to_user_id,
                status, created_at, updated_at
            )
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING id, from_user_id, to_user_id,
                      status, created_at, updated_at
            """;

    private static final String OUTOFFER = """
            SELECT id, from_user_id, to_user_id,
                      status, created_at, updated_at
            FROM offer_friendships
            WHERE from_user_id = ?
            ORDER BY created_at DESC, id DESC
            """;

    private static final String INOFFER = """
            SELECT id, from_user_id, to_user_id,
                      status, created_at, updated_at
            FROM offer_friendships
            WHERE to_user_id = ?
            ORDER BY created_at DESC, id DESC
            """;

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<OfferFriendshipEntity> OFFER_ROW_MAPPER = (rs, rowNum) -> {
        OfferFriendshipEntity offer = new OfferFriendshipEntity();
        offer.setId(rs.getObject("id", UUID.class));
        offer.setFromUserId(rs.getObject("from_user_id", UUID.class));
        offer.setToUserId(rs.getObject("to_user_id", UUID.class));
        offer.setStatus(OfferFriendshipEntity.Status.valueOf(rs.getString("status")));
        offer.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class).toInstant());
        offer.setUpdatedAt(rs.getObject("updated_at", OffsetDateTime.class).toInstant());
        return offer;
    };

    public JdbcOfferFriendshipRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CreateOfferFriendshipResponse createOfferFriendship(
            CreateOfferFriendshipRequest request
    ) {
        try {
            return jdbcTemplate.queryForObject(
                    CREATE,
                    (resultSet, rowNum) -> new CreateOfferFriendshipResponse(
                            resultSet.getObject("id", java.util.UUID.class),
                            resultSet.getObject("from_user_id", java.util.UUID.class),
                            resultSet.getObject("to_user_id", java.util.UUID.class),
                            Status.valueOf(resultSet.getString("status")),
                            resultSet.getTimestamp("created_at").toInstant(),
                            resultSet.getTimestamp("updated_at").toInstant()
                    ),
                    request.id(),
                    request.fromUserId(),
                    request.toUserId(),
                    request.status().name(),
                    java.sql.Timestamp.from(request.createdAt()),
                    java.sql.Timestamp.from(request.updatedAt())
            );
        } catch (DataIntegrityViolationException e) {
            var message = e.getMostSpecificCause().getMessage();
            if (message != null && message.contains("chk_offer_friendships_different_users")) {
                throw new SelfFriendshipException(message);
            }
            if (message != null && message.contains("uq_offer_friendships_users")) {
                throw new OfferFriendshipAlreadyExistsException(message);
            }
            throw e;
        }
    }

    @Override
    public List<OfferFriendshipEntity> findIncomingOfferFriendships(UUID userId) {
        return jdbcTemplate.query(INOFFER, OFFER_ROW_MAPPER, userId);
    }

    @Override
    public List<OfferFriendshipEntity> findOutgoingOfferFriendships(UUID userId) {
        return jdbcTemplate.query(OUTOFFER, OFFER_ROW_MAPPER, userId);
    }
}
