package ru.job4j.socialmedia.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.socialmedia.controller.api.FriendshipApi;
import ru.job4j.socialmedia.controller.dto.CreateOfferFriendshipRequest;
import ru.job4j.socialmedia.controller.dto.CreateOfferFriendshipResponse;
import ru.job4j.socialmedia.entity.OfferFriendshipEntity;
import ru.job4j.socialmedia.service.FriendshipService;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RestController
public class FriendshipController implements FriendshipApi {

    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService) {

        this.friendshipService = friendshipService;

    }

    @Override
    public ResponseEntity<CreateOfferFriendshipResponse> createOfferFriendship(
            CreateOfferFriendshipRequest request) {
        var result = friendshipService.createOfferFriendship(
                request.getFromUserId(),
                request.getToUserId()
        );
        if (result.isPresent()) {
            var response = toResponse(result.get());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        return ResponseEntity.noContent().build();
    }

    private CreateOfferFriendshipResponse toResponse(OfferFriendshipEntity entity) {
        var r = new CreateOfferFriendshipResponse();
        r.setOfferId(entity.getId());
        r.setFromUserId(entity.getFromUserId());
        r.setToUserId(entity.getToUserId());
        r.setStatus(CreateOfferFriendshipResponse.StatusEnum.valueOf(entity.getStatus().name()));
        r.setCreatedAt(OffsetDateTime.ofInstant(entity.getCreatedAt(), ZoneOffset.UTC));
        return r;
    }
}