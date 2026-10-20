--liquibase formatted sql
--changeset student:007
CREATE TABLE posts (
                       id          UUID PRIMARY KEY,
                       author_id   UUID NOT NULL,
                       title       VARCHAR(255) NOT NULL,
                       content     TEXT,
                       status      VARCHAR(10) NOT NULL,
                       created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       CONSTRAINT posts_status_check
                           CHECK (status IN ('PUBLISHED' , 'DELETED'))
);

--rollback DROP TABLE posts;