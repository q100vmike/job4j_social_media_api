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
public class JdbcFeedRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17");

    @Autowired
    private FeedRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UUID alice;
    private UUID bob;
    private UUID carol;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM subscriptions");
        jdbcTemplate.update("DELETE FROM posts");

        alice = UUID.randomUUID();
        bob = UUID.randomUUID();
        carol = UUID.randomUUID();

        insertSubscription(alice, bob);
        insertSubscription(alice, carol);
        insertSubscription(bob, carol);
    }

    @Test
    void whenFindFeedThenReturnPostsOfFollowedAuthors() {
        var bobPostId = insertPost(bob, "bob post", PostEntity.Status.PUBLISHED,
                instant("2026-01-01T10:00:00Z"));
        var carolPostId = insertPost(carol, "carol post", PostEntity.Status.PUBLISHED,
                instant("2026-01-02T10:00:00Z"));

        var list = repository.findFeed(alice, 10, 0);

        assertThat(list).extracting(PostEntity::getId)
                .containsExactly(carolPostId, bobPostId); // createdAt DESC
    }

    @Test
    void whenPostIsDeletedThenItIsNotInFeed() {
        var publishedId = insertPost(bob, "published", PostEntity.Status.PUBLISHED,
                instant("2026-01-01T10:00:00Z"));
        insertPost(bob, "deleted", PostEntity.Status.DELETED,
                instant("2026-01-02T10:00:00Z"));

        var list = repository.findFeed(alice, 10, 0);

        assertThat(list).extracting(PostEntity::getId).containsExactly(publishedId);
    }

    @Test
    void whenAuthorIsNotFollowedThenPostIsNotInFeed() {
        insertPost(bob, "bob post", PostEntity.Status.PUBLISHED,
                instant("2026-01-01T10:00:00Z"));
        var ownPostId = insertPost(carol, "carol post", PostEntity.Status.PUBLISHED,
                instant("2026-01-02T10:00:00Z"));

        var list = repository.findFeed(carol, 10, 0);

        assertThat(list).extracting(PostEntity::getId).doesNotContain(ownPostId);
    }

    @Test
    void whenUserFollowsNobodyThenFeedIsEmpty() {
        insertPost(bob, "bob post", PostEntity.Status.PUBLISHED,
                instant("2026-01-01T10:00:00Z"));
        var loner = UUID.randomUUID();

        var list = repository.findFeed(loner, 10, 0);

        assertThat(list).isEmpty();
    }

    @Test
    void whenLimitAndOffsetAppliedThenPaginationWorks() {
        insertPost(bob, "first", PostEntity.Status.PUBLISHED,
                instant("2026-01-01T10:00:00Z"));
        var secondId = insertPost(bob, "second", PostEntity.Status.PUBLISHED,
                instant("2026-01-02T10:00:00Z"));
        insertPost(bob, "third", PostEntity.Status.PUBLISHED,
                instant("2026-01-03T10:00:00Z"));

        var page = repository.findFeed(alice, 1, 1);

        assertThat(page).extracting(PostEntity::getId).containsExactly(secondId);
    }

    @Test
    void whenFindFeedThenAllFieldsMapped() {
        var postId = insertPost(bob, "title", PostEntity.Status.PUBLISHED,
                instant("2026-01-01T10:00:00Z"));

        var post = repository.findFeed(alice, 10, 0).get(0);

        var expected = new PostEntity();
        expected.setId(postId);
        expected.setAuthorId(bob);
        expected.setTitle("title");
        expected.setContent("content");
        expected.setStatus(PostEntity.Status.PUBLISHED);
        expected.setCreatedAt(offset("2026-01-01T10:00:00Z"));
        expected.setUpdatedAt(offset("2026-01-01T10:00:00Z"));

        assertThat(post).isEqualTo(expected);
    }

    private Instant instant(String value) {
        return Instant.parse(value);
    }

    private OffsetDateTime offset(String value) {
        return OffsetDateTime.ofInstant(Instant.parse(value), ZoneOffset.UTC);
    }

    private void insertSubscription(UUID followerId, UUID followedId) {
        jdbcTemplate.update("""
                INSERT INTO subscriptions (id, follower_id, followed_id, created_at)
                VALUES (?, ?, ?, now())
                """, UUID.randomUUID(), followerId, followedId);
    }

    private UUID insertPost(UUID authorId, String title, PostEntity.Status status,
                            Instant createdAt) {
        var id = UUID.randomUUID();
        jdbcTemplate.update("""
                INSERT INTO posts (id, author_id, title, content, status, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """, id, authorId, title, "content", status.name(), Timestamp.from(createdAt), Timestamp.from(createdAt));
        return id;
    }
}