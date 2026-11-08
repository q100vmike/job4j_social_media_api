package ru.job4j.socialmedia.exception;

public class FriendshipWrongStatusException extends RuntimeException {
    public FriendshipWrongStatusException(String message) {
        super(message);
    }
}