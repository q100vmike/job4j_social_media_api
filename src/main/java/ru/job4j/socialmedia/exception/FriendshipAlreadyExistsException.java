package ru.job4j.socialmedia.exception;

public class FriendshipAlreadyExistsException extends RuntimeException{

    public FriendshipAlreadyExistsException(String message) {
        super(message);
    }
}
