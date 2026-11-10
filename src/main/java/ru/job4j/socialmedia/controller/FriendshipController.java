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
        if (result.isPresent()) {
            var response = presenter.toResponse(result.get());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> acceptOfferFriendship(AcceptOfferFriendshipRequest request) {
        throw new UnsupportedOperationException("acceptOfferFriendship not implemented yet");
    }

    @Override
    public ResponseEntity<Void> rejectOfferFriendship(RejectOfferFriendshipRequest request) {
        throw new UnsupportedOperationException("rejectOfferFriendship not implemented yet");
    }

    @Override
    public ResponseEntity<List<UserResponse>> getFriendships(UUID userId) {
        throw new UnsupportedOperationException("getFriendships not implemented yet");
    }

    @Override
    public ResponseEntity<List<OfferFriendshipResponse>> getIncomingOfferFriendships(UUID userId) {
        throw new UnsupportedOperationException("getIncomingOfferFriendships not implemented yet");
    }

    @Override
    public ResponseEntity<List<OfferFriendshipResponse>> getOutgoingOfferFriendships(UUID userId) {
        throw new UnsupportedOperationException("getOutgoingOfferFriendships not implemented yet");
    }
}