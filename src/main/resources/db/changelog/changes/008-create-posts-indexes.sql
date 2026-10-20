--liquibase formatted sql

--changeset student:008

CREATE INDEX idx_posts_author_created_id
    ON posts (author_id, created_at DESC, id DESC);

--rollback DROP INDEX idx_subscriptions_follower;
--rollback DROP INDE