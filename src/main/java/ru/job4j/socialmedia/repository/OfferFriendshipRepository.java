package ru.job4j.socialmedia.repository;

import java.time.Instant;
import java.util.UUID;

public interface OfferFriendshipRepository {
    CreateOfferFriendshipResponse createOfferFriendship(
            CreateOfferFriendshipRequest request
    );

    record CreateOfferFriendshipRequest(
            UUID id,
            UUID fromUserId,
            UUID toUserId,
            Status status,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

    record CreateOfferFriendshipResponse(
            UUID id,
            UUID fromUserId,
            UUID toUserId,
            Status status,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

    enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }
}