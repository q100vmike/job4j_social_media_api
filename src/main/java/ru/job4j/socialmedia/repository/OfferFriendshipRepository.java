package ru.job4j.socialmedia.repository;

import ru.job4j.socialmedia.entity.OfferFriendshipEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OfferFriendshipRepository {

    CreateOfferFriendshipResponse createOfferFriendship(
            CreateOfferFriendshipRequest request
    );

    record CreateOfferFriendshipRequest(
            UUID id,
            UUID fromUserId,
            UUID toUserId,
            OfferFriendshipEntity.Status status,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

    record CreateOfferFriendshipResponse(
            UUID id,
            UUID fromUserId,
            UUID toUserId,
            OfferFriendshipEntity.Status status,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

    List<OfferFriendshipEntity> findIncomingOfferFriendships(
            UUID userId
    );

    List<OfferFriendshipEntity> findOutgoingOfferFriendships(
            UUID userId
    );

    Optional<OfferFriendshipEntity> findByFromAndTo(UUID fromUserId, UUID toUserId);
}