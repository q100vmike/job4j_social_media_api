package ru.job4j.socialmedia.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.job4j.socialmedia.entity.PostEntity;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcPostRepository implements PostRepository {

    private static final String CREATE = """
            INSERT INTO posts (
                id, author_id, title, content, status, created_at, updated_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING id, author_id, title, content, status, created_at, updated_at
            """;

    private static final String MARK_AS_DELETED = """
            UPDATE posts
            SET status = 'DELETED',
                updated_at = ?
            WHERE id = ?
              AND status = 'PUBLISHED'
            """;

    private static final String UPDATE = """
            UPDATE posts
            SET title = ?,
                content = ?,
                updated_at = ?
            WHERE id = ?
              AND status = 'PUBLISHED'
            RETURNING id, author_id, title, content, status, created_at, updated_at
            """;

    private static final RowMapper<CreatePostResponse> POST_ROW_MAPPER = (rs, rowNum) -> new CreatePostResponse(
            rs.getObject("id", UUID.class),
            rs.getObject("author_id", UUID.class),
            rs.getString("title"),
            rs.getString("content"),
            PostEntity.Status.valueOf(rs.getString("status")),
            rs.getObject("created_at", OffsetDateTime.class).toInstant(),
            rs.getObject("updated_at", OffsetDateTime.class).toInstant()
    );

    private final JdbcTemplate jdbcTemplate;

    public JdbcPostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CreatePostResponse createPost(CreatePostRequest request) {
        return jdbcTemplate.queryForObject(
                CREATE,
                POST_ROW_MAPPER,
                request.id(),
                request.authorId(),
                request.title(),
                request.content(),
                request.status().name(),
                java.sql.Timestamp.from(request.createdAt()),
                java.sql.Timestamp.from(request.updatedAt())
        );
    }

    @Override
    public boolean markAsDeleted(UUID id, Instant updatedAt) {
        int updated = jdbcTemplate.update(
                MARK_AS_DELETED,
                java.sql.Timestamp.from(updatedAt),
                id
        );
        return updated > 0;
    }

    @Override
    public Optional<CreatePostResponse> updatePost(UpdatePostRequest request) {
        return jdbcTemplate.query(
                UPDATE,
                POST_ROW_MAPPER,
                request.title(),
                request.content(),
                java.sql.Timestamp.from(request.updatedAt()),
                request.id()
        ).stream().findFirst();
    }
}