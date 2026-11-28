package ru.job4j.socialmedia.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.job4j.socialmedia.exception.FriendshipWrongStatusException;
import ru.job4j.socialmedia.exception.OfferFriendshipNotFoundException;
import ru.job4j.socialmedia.exception.SelfFriendshipException;

import java.net.URI;

@RestControllerAdvice(assignableTypes = FriendshipController.class)
public class FriendshipExceptionHandler {

    @ExceptionHandler(SelfFriendshipException.class)
    public ProblemDetail handleSelfFriendship(SelfFriendshipException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Bad Request");
        problem.setDetail(e.getMessage());
        problem.setInstance(URI.create("/api/offer-friendships/create"));
        return problem;
    }

    @ExceptionHandler(OfferFriendshipNotFoundException.class)
    public ProblemDetail handleOfferNotFound(OfferFriendshipNotFoundException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("No Friendship Exist");
        problem.setDetail(e.getMessage());
        problem.setInstance(URI.create("/api/offer-friendships/accept"));
        return problem;
    }

    @ExceptionHandler(FriendshipWrongStatusException.class)
    public ProblemDetail handleWrongStatus(FriendshipWrongStatusException e) {
        var problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Friendship wrong status");
        problem.setDetail(e.getMessage());
        problem.setInstance(URI.create("/api/offer-friendships/create"));
        return problem;
    }
}