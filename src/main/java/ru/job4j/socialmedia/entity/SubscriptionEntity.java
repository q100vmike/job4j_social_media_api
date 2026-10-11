package ru.job4j.socialmedia.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class SubscriptionEntity {

    private UUID id;
    private UUID followerId;
    private UUID followedId;
    private Instant createdAt;

    public SubscriptionEntity() {
    }

    public SubscriptionEntity(
            UUID id,
            UUID followerId,
            UUID followedId,
            Instant createdAt
    ) {
        this.id = id;
        this.followerId = followerId;
        this.followedId = followedId;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFollowerId() {
        return followerId;
    }

    public void setFollowerId(UUID followerId) {
        this.followerId = followerId;
    }

    public UUID getFollowedId() {
        return followedId;
    }

    public void setFollowedId(UUID followedId) {
        this.followedId = followedId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubscriptionEntity that)) {
            return false;
        }
        return Objects.equals(followerId, that.followerId)
                && Objects.equals(followedId, that.followedId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerId, followedId);
    }

    @Override
    public String toString() {
        return "SubscriptionEntity{"
                + "id=" + id
                + ", followerId=" + followerId
                + ", followedId=" + followedId
                + ", createdAt=" + createdAt
                + '}';
    }
}