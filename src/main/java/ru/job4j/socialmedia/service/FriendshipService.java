package ru.job4j.socialmedia.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.socialmedia.entity.OfferFriendshipEntity;
import ru.job4j.socialmedia.exception.FriendshipWrongStatusException;
import ru.job4j.socialmedia.exception.OfferFriendshipAlreadyExistsException;
import ru.job4j.socialmedia.exception.SelfFriendshipException;
import ru.job4j.socialmedia.repository.OfferFriendshipRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class FriendshipService {

    private final OfferFriendshipRepository offerFriendshipRepository;

    public FriendshipService(OfferFriendshipRepository offerFriendshipRepository) {
        this.offerFriendshipRepository = offerFriendshipRepository;
    }

    @Transactional
    public Optional<OfferFriendshipEntity> createOfferFriendship(UUID fromUserId, UUID toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new SelfFriendshipException("Нельзя отправить заявку самому себе");
        }

        var existing = offerFriendshipRepository.findByFromAndTo(fromUserId, toUserId);
        if (existing.isPresent()) {
            var status = existing.get().getStatus();
            if (status == OfferFriendshipEntity.Status.PENDING
                    || status == OfferFriendshipEntity.Status.ACCEPTED) {
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
                            OfferFriendshipEntity.Status.PENDING,
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
}