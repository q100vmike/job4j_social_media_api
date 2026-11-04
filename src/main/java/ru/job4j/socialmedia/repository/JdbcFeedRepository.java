package ru.job4j.socialmedia.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.job4j.socialmedia.entity.PostEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class JdbcFeedRepository implements FeedRepository {

    private static final String FIND_FEED = """
            SELECT p.id,
                   p.author_id,
                   p.title,
                   p.content,
                   p.status,
                   p.created_at,
                   p.updated_at
            FROM subscriptions s
            JOIN posts p ON p.author_id = s.followed_id
            WHERE s.follower_id = ?
              AND p.status = 'PUBLISHED'
            ORDER BY p.created_at DESC, p.id DESC
            LIMIT ? OFFSET ?
            """;

    private static final RowMapper<PostEntity> POST_ROW_MAPPER = (rs, rowNum) -> {
        PostEntity post = new PostEntity();
        post.setId(rs.getObject("id", UUID.class));
        post.setAuthorId(rs.getObject("author_id", UUID.class));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        post.setStatus(PostEntity.Status.valueOf(rs.getString("status")));
        post.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
        post.setUpdatedAt(rs.getObject("updated_at", OffsetDateTime.class));
        return post;
    };

    private final JdbcTemplate jdbcTemplate;

    public JdbcFeedRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<PostEntity> findFeed(UUID userId, int limit, int offset) {
        return jdbcTemplate.query(
                FIND_FEED,
                POST_ROW_MAPPER,
                userId,
                limit,
                offset
        );
    }
}
