--liquibase formatted sql

--changeset student:005
CREATE TABLE subscriptions (
                               id UUID NOT NULL,
                               follower_id UUID NOT NULL,
                               followed_id UUID NOT NULL,
                               created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               PRIMARY KEY (follower_id, followed_id),
                               CONSTRAINT chk_subscription_users CHECK (follower_id <> followed_id)
);

--rollback DROP TABLE subscriptions;