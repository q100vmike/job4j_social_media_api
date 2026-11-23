package ru.job4j.socialmedia.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.job4j.socialmedia.controller.api.FriendshipApi;
import ru.job4j.socialmedia.controller.dto.AcceptOfferFriendshipRequest;
import ru.job4j.socialmedia.controller.dto.CreateOfferFriendshipRequest;
import ru.job4j.socialmedia.controller.dto.CreateOfferFriendshipResponse;
import ru.job4j.socialmedia.controller.dto.OfferFriendshipResponse;
import ru.job4j.socialmedia.controller.dto.RejectOfferFriendshipRequest;
import ru.job4j.socialmedia.controller.dto.UserResponse;
import ru.job4j.socialmedia.controller.presenter.OfferFriendshipPresenter;
import ru.job4j.socialmedia.service.FriendshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class FriendshipController implements FriendshipApi {

    private final FriendshipService friendshipService;
    private final OfferFriendshipPresenter presenter;

    public FriendshipController(FriendshipService friendshipService,
                                OfferFriendshipPresenter presenter) {
        this.friendshipService = friendshipService;
        this.presenter = presenter;
    }

    @Override
    public ResponseEntity<CreateOfferFriendshipResponse> createOfferFriendship(
            CreateOfferFriendshipRequest request) {
        var result = friendshipService.createOfferFriendship(
                request.getFromUserId(),
                request.getToUserId()
        );
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        var response = presenter.toCreateResponse(result.get());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> acceptOfferFriendship(AcceptOfferFriendshipRequest request) {
        var result = friendshipService.acceptOfferFriendship(request.getOfferId());
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> rejectOfferFriendship(RejectOfferFriendshipRequest request) {
        var result = friendshipService.rejectOfferFriendship(request.getOfferId());
        if (result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<UserResponse>> getFriendships(UUID userId) {
        var friendships = friendshipService.findFriendships(userId);
        var response = new ArrayList<UserResponse>(friendships.size());
        for (var friendship : friendships) {
            var friendId = friendship.getFirstUserId().equals(userId)
                    ? friendship.getSecondUserId()
                    : friendship.getFirstUserId();
            var user = new UserResponse();
            user.setId(friendId);
            response.add(user);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<OfferFriendshipResponse>> getIncomingOfferFriendships(UUID userId) {
        var offers = friendshipService.findIncomingOfferFriendships(userId);
        var response = new ArrayList<OfferFriendshipResponse>(offers.size());
        for (var offer : offers) {
            response.add(presenter.toResponse(offer));
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<OfferFriendshipResponse>> getOutgoingOfferFriendships(UUID userId) {
        var offers = friendshipService.findOutgoingOfferFriendships(userId);
        var response = new ArrayList<OfferFriendshipResponse>(offers.size());
        for (var offer : offers) {
            response.add(presenter.toResponse(offer));
        }
        return ResponseEntity.ok(response);
    }
}