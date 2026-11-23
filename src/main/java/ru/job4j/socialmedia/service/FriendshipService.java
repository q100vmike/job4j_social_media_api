package ru.job4j.socialmedia.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.socialmedia.entity.FriendshipEntity;
import ru.job4j.socialmedia.entity.OfferFriendshipEntity;
import ru.job4j.socialmedia.exception.FriendshipWrongStatusException;
import ru.job4j.socialmedia.exception.OfferFriendshipAlreadyExistsException;
import ru.job4j.socialmedia.exception.OfferFriendshipNotFoundException;
import ru.job4j.socialmedia.exception.SelfFriendshipException;
import ru.job4j.socialmedia.repository.FriendshipRepository;
import ru.job4j.socialmedia.repository.OfferFriendshipRepository;
import ru.job4j.socialmedia.repository.SubscriptionRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.job4j.socialmedia.entity.OfferFriendshipEntity.Status.ACCEPTED;
import static ru.job4j.socialmedia.entity.OfferFriendshipEntity.Status.PENDING;
import static ru.job4j.socialmedia.entity.OfferFriendshipEntity.Status.REJECTED;

@Service
public class FriendshipService {

    private final OfferFriendshipRepository offerFriendshipRepository;
    private final FriendshipRepository friendshipRepository;
    private final SubscriptionRepository subscriptionRepository;

    public FriendshipService(OfferFriendshipRepository offerFriendshipRepository,
                             FriendshipRepository friendshipRepository,
                             SubscriptionRepository subscriptionRepository) {
        this.offerFriendshipRepository = offerFriendshipRepository;
        this.friendshipRepository = friendshipRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public Optional<OfferFriendshipEntity> createOfferFriendship(UUID fromUserId, UUID toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new SelfFriendshipException("Нельзя отправить заявку самому себе");
        }

        var existing = offerFriendshipRepository.findByFromAndTo(fromUserId, toUserId);
        if (existing.isPresent()) {
            var status = existing.get().getStatus();
            if (status == PENDING || status == ACCEPTED) {
                return Optional.empty();
            }
            throw new FriendshipWrongStatusException(
                    "Заявка уже существует в статусе " + status);
        }

        var now = Instant.now();
        try {
            var created = offerFriendshipRepository.createOfferFriendship(
                    new OfferFriendshipRepository.CreateOfferFriendshipRequest(
                            UUID.randomUUID(),
                            fromUserId,
                            toUserId,
                            PENDING,
                            now,
                            now
                    )
            );
            return Optional.of(toEntity(created));
        } catch (OfferFriendshipAlreadyExistsException e) {
            return Optional.empty();
        }
    }

    private OfferFriendshipEntity toEntity(
            OfferFriendshipRepository.CreateOfferFriendshipResponse r) {
        var e = new OfferFriendshipEntity();
        e.setId(r.id());
        e.setFromUserId(r.fromUserId());
        e.setToUserId(r.toUserId());
        e.setStatus(r.status());
        e.setCreatedAt(r.createdAt());
        e.setUpdatedAt(r.updatedAt());
        return e;
    }

    @Transactional
    public Optional<OfferFriendshipEntity> acceptOfferFriendship(UUID offerId) {
        var offer = offerFriendshipRepository.findById(offerId)
                .orElseThrow(() -> new OfferFriendshipNotFoundException(
                        "Предложение дружбы не найдено"));

        if (offer.getStatus() == ACCEPTED) {
            return Optional.empty();   // 204
        }
        if (offer.getStatus() != PENDING) {
            throw new FriendshipWrongStatusException(
                    "Заявка в статусе " + offer.getStatus());   // 409
        }

        var now = Instant.now();
        if (!offerFriendshipRepository.updatePendingStatus(offerId, ACCEPTED, now)) {
            var current = offerFriendshipRepository.findById(offerId)
                    .orElseThrow(() -> new OfferFriendshipNotFoundException(
                            "Предложение дружбы исчезло: " + offerId));
            if (current.getStatus() == ACCEPTED) {
                return Optional.empty();   // 204
            }
            throw new FriendshipWrongStatusException(
                    "Заявка в статусе " + current.getStatus());   // 409
        }

        // при принятии создаём дружбу и подписки в обе стороны
        friendshipRepository.createFriendship(
                new FriendshipRepository.CreateFriendshipRequest(
                        UUID.randomUUID(),
                        offer.getFromUserId(),
                        offer.getToUserId(),
                        offerId,
                        now
                )
        );
        subscriptionRepository.createSubscription(
                new SubscriptionRepository.CreateSubscriptionRequest(
                        UUID.randomUUID(),
                        offer.getFromUserId(),
                        offer.getToUserId(),
                        now
                )
        );
        subscriptionRepository.createSubscription(
                new SubscriptionRepository.CreateSubscriptionRequest(
                        UUID.randomUUID(),
                        offer.getToUserId(),
                        offer.getFromUserId(),
                        now
                )
        );

        offer.setStatus(ACCEPTED);
        offer.setUpdatedAt(now);
        return Optional.of(offer);   // 200
    }

    @Transactional
    public Optional<OfferFriendshipEntity> rejectOfferFriendship(UUID offerId) {
        var offer = offerFriendshipRepository.findById(offerId)
                .orElseThrow(() -> new OfferFriendshipNotFoundException(
                        "Предложение дружбы не найдено"));

        if (offer.getStatus() == REJECTED) {
            return Optional.empty();   // 204
        }
        if (offer.getStatus() != PENDING) {
            throw new FriendshipWrongStatusException(
                    "Заявка в статусе " + offer.getStatus());   // 409
        }

        var now = Instant.now();
        if (!offerFriendshipRepository.updatePendingStatus(offerId, REJECTED, now)) {
            var current = offerFriendshipRepository.findById(offerId)
                    .orElseThrow(() -> new OfferFriendshipNotFoundException(
                            "Предложение дружбы исчезло: " + offerId));
            if (current.getStatus() == REJECTED) {
                return Optional.empty();   // 204
            }
            throw new FriendshipWrongStatusException(
                    "Заявка в статусе " + current.getStatus());   // 409
        }

        offer.setStatus(REJECTED);
        offer.setUpdatedAt(now);
        return Optional.of(offer);   // 200
    }

    @Transactional(readOnly = true)
    public List<OfferFriendshipEntity> findIncomingOfferFriendships(UUID userId) {
        return offerFriendshipRepository.findIncomingOfferFriendships(userId);
    }

    @Transactional(readOnly = true)
    public List<OfferFriendshipEntity> findOutgoingOfferFriendships(UUID userId) {
        return offerFriendshipRepository.findOutgoingOfferFriendships(userId);
    }

    @Transactional(readOnly = true)
    public List<FriendshipEntity> findFriendships(UUID userId) {
        return friendshipRepository.findFriendships(userId);
    }
}