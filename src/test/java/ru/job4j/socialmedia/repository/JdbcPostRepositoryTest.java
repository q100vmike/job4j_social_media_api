package ru.job4j.socialmedia.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import ru.job4j.socialmedia.entity.PostEntity;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
public class JdbcPostRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17");

    @Autowired
    private PostRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM posts");
    }

    @Test
    void whenCreatePostThenReturnStoredData() {
        var id = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var now = Instant.parse("2026-01-01T10:00:00Z");

        var request = new PostRepository.CreatePostRequest(
                id, authorId, "title", "content",
                PostEntity.Status.PUBLISHED, now, now
        );

        var result = repository.createPost(request);

        var expected = new PostRepository.CreatePostResponse(
                id, authorId, "title", "content",
                PostEntity.Status.PUBLISHED, now, now
        );
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void whenCreateTwoPostsThenReturnBoth() {
        var authorId = UUID.randomUUID();
        var now = Instant.parse("2026-01-01T10:00:00Z");

        var first = repository.createPost(new PostRepository.CreatePostRequest(
                UUID.randomUUID(), authorId, "first", "content",
                PostEntity.Status.PUBLISHED, now, now
        ));
        var second = repository.createPost(new PostRepository.CreatePostRequest(
                UUID.randomUUID(), authorId, "second", "content",
                PostEntity.Status.PUBLISHED, now, now
        ));

        assertThat(first.id()).isNotEqualTo(second.id());
        assertThat(countPosts()).isEqualTo(2);
    }

    @Test
    void whenMarkAsDeletedPublishedPostThenStatusChanged() {
        var id = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var createdAt = Instant.parse("2026-01-01T10:00:00Z");
        repository.createPost(new PostRepository.CreatePostRequest(
                id, authorId, "title", "content",
                PostEntity.Status.PUBLISHED, createdAt, createdAt
        ));

        var deletedAt = Instant.parse("2026-01-02T10:00:00Z");
        var result = repository.markAsDeleted(id, deletedAt);

        assertThat(result).isTrue();
        var stored = findBy(id);
        assertThat(stored.getStatus()).isEqualTo(PostEntity.Status.DELETED);
        assertThat(stored.getUpdatedAt().toInstant()).isEqualTo(deletedAt);
    }

    @Test
    void whenMarkAsDeletedAlreadyDeletedPostThenReturnFalse() {
        var id = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var createdAt = Instant.parse("2026-01-01T10:00:00Z");
        repository.createPost(new PostRepository.CreatePostRequest(
                id, authorId, "title", "content",
                PostEntity.Status.PUBLISHED, createdAt, createdAt
        ));
        repository.markAsDeleted(id, Instant.parse("2026-01-02T10:00:00Z"));

        var result = repository.markAsDeleted(id, Instant.parse("2026-01-03T10:00:00Z"));

        assertThat(result).isFalse();
    }

    @Test
    void whenMarkAsDeletedMissingPostThenReturnFalse() {
        var result = repository.markAsDeleted(UUID.randomUUID(), Instant.now());

        assertThat(result).isFalse();
    }

    @Test
    void whenUpdatePostThenReturnUpdatedData() {
        var id = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var createdAt = Instant.parse("2026-01-01T10:00:00Z");
        repository.createPost(new PostRepository.CreatePostRequest(
                id, authorId, "old title", "old content",
                PostEntity.Status.PUBLISHED, createdAt, createdAt
        ));

        var updatedAt = Instant.parse("2026-01-02T10:00:00Z");
        var result = repository.updatePost(new PostRepository.UpdatePostRequest(
                id, "new title", "new content", updatedAt
        ));

        assertThat(result).isPresent();
        var updated = result.get();
        assertThat(updated.id()).isEqualTo(id);
        assertThat(updated.authorId()).isEqualTo(authorId);
        assertThat(updated.title()).isEqualTo("new title");
        assertThat(updated.content()).isEqualTo("new content");
        assertThat(updated.status()).isEqualTo(PostEntity.Status.PUBLISHED);
        assertThat(updated.createdAt()).isEqualTo(createdAt);
        assertThat(updated.updatedAt()).isEqualTo(updatedAt);
    }

    @Test
    void whenUpdateDeletedPostThenReturnEmpty() {
        var id = UUID.randomUUID();
        var authorId = UUID.randomUUID();
        var createdAt = Instant.parse("2026-01-01T10:00:00Z");
        repository.createPost(new PostRepository.CreatePostRequest(
                id, authorId, "title", "content",
                PostEntity.Status.PUBLISHED, createdAt, createdAt
        ));
        repository.markAsDeleted(id, Instant.parse("2026-01-02T10:00:00Z"));

        var result = repository.updatePost(new PostRepository.UpdatePostRequest(
                id, "new title", "new content",
                Instant.parse("2026-01-03T10:00:00Z")
        ));

        assertThat(result).isEmpty();
    }

    @Test
    void whenUpdateMissingPostThenReturnEmpty() {
        var result = repository.updatePost(new PostRepository.UpdatePostRequest(
                UUID.randomUUID(), "title", "content", Instant.now()
        ));

        assertThat(result).isEmpty();
    }

    private int countPosts() {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM posts", Integer.class);
    }

    private PostEntity findBy(UUID id) {
        return jdbcTemplate.queryForObject(
                """
                SELECT id, author_id, title, content, status, created_at, updated_at
                FROM posts
                WHERE id = ?
                """,
                (rs, rowNum) -> {
                    var post = new PostEntity();
                    post.setId(rs.getObject("id", UUID.class));
                    post.setAuthorId(rs.getObject("author_id", UUID.class));
                    post.setTitle(rs.getString("title"));
                    post.setContent(rs.getString("content"));
                    post.setStatus(PostEntity.Status.valueOf(rs.getString("status")));
                    post.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
                    post.setUpdatedAt(rs.getObject("updated_at", OffsetDateTime.class));
                    return post;
                },
                id
        );
    }
}