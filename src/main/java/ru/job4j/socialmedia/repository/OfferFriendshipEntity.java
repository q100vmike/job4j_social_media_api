package ru.job4j.socialmedia.repository;

import java.time.Instant;
import java.util.UUID;

record OfferFriendshipEntity(
        UUID id,
        UUID fromUserId,
        UUID toUserId,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
