package ru.job4j.socialmedia.repository;

import ru.job4j.socialmedia.entity.SubscriptionEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface SubscriptionRepository {

    CreateSubscriptionResponse createSubscription(
            CreateSubscriptionRequest request
    );

    record CreateSubscriptionRequest(
            UUID id,
            UUID followerId,
            UUID followedId,
            Instant createdAt
    ) {
    }

    record CreateSubscriptionResponse(
            UUID id,
            UUID followerId,
            UUID followedId,
            Instant createdAt
    ) {
    }

    List<SubscriptionEntity> findSubscriptions(
            UUID userId
    );

    List<SubscriptionEntity> findFollowers(
            UUID userId
    );
}
