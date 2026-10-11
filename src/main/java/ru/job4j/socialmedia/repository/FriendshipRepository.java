package ru.job4j.socialmedia.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface FriendshipRepository {
    FriendshipRepository.CreateFriendshipResponse createFriendship(
            FriendshipRepository.CreateFriendshipRequest request
    );

    record CreateFriendshipRequest(
            UUID id,
            UUID firstUserId,
            UUID secondUserId,
            UUID offerFriendshipId,
            Instant createdAt
    ) {
    }

    record CreateFriendshipResponse(
            UUID id,
            UUID firstUserId,
            UUID secondUserId,
            UUID offerFriendshipId,
            Instant createdAt
    ) {
    }

    List<UUID> findFriendships(
            UUID userId
    );
}
