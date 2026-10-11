package ru.job4j.socialmedia.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class FriendshipEntity {

    private UUID id;
    private UUID firstUserId;
    private UUID secondUserId;
    private UUID offerFriendshipId;
    private Instant createdAt;

    public FriendshipEntity() {
    }

    public FriendshipEntity(
            UUID id,
            UUID firstUserId,
            UUID secondUserId,
            UUID offerFriendshipId,
            Instant createdAt
    ) {
        this.id = id;
        this.firstUserId = firstUserId;
        this.secondUserId = secondUserId;
        this.offerFriendshipId = offerFriendshipId;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFirstUserId() {
        return firstUserId;
    }

    public void setFirstUserId(UUID firstUserId) {
        this.firstUserId = firstUserId;
    }

    public UUID getSecondUserId() {
        return secondUserId;
    }

    public void setSecondUserId(UUID secondUserId) {
        this.secondUserId = secondUserId;
    }

    public UUID getOfferFriendshipId() {
        return offerFriendshipId;
    }

    public void setOfferFriendshipId(UUID offerFriendshipId) {
        this.offerFriendshipId = offerFriendshipId;
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
        if (!(o instanceof FriendshipEntity that)) {
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
        return "FriendshipEntity{"
                + "id=" + id
                + ", firstUserId=" + firstUserId
                + ", secondUserId=" + secondUserId
                + ", offerFriendshipId=" + offerFriendshipId
                + ", createdAt=" + createdAt
                + '}';
    }
}