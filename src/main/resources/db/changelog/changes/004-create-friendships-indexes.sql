--liquibase formatted sql

--changeset student:004
CREATE INDEX idx_friendships_first
    ON friendships (first_user_id);

CREATE INDEX idx_friendships_second
    ON friendships (second_user_id);

--rollback DROP INDEX idx_friendships_first;
--rollback DROP INDEX idx_friendships_second;

