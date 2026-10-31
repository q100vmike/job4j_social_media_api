package ru.job4j.socialmedia.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class OfferFriendshipEntity {

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    private UUID id;
    private UUID fromUserId;
    private UUID toUserId;
    private Status status;
    private Instant createdAt;
    private Instant updatedAt;

    public OfferFriendshipEntity() {
    }

    public OfferFriendshipEntity(
            UUID id,
            UUID fromUserId,
            UUID toUserId,
            Status status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(UUID fromUserId) {
        this.fromUserId = fromUserId;
    }

    public UUID getToUserId() {
        return toUserId;
    }

    public void setToUserId(UUID toUserId) {
        this.toUserId = toUserId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OfferFriendshipEntity that)) {
            return false;
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OfferFriendshipEntity{"
               + "id=" + id
               + ", fromUserId=" + fromUserId
               + ", toUserId=" + toUserId
               + ", status=" + status
               + ", createdAt=" + createdAt
               + ", updatedAt=" + updatedAt
               + '}';
    }
}