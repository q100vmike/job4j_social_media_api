package ru.job4j.socialmedia.exception;

public class SelfSubscriptionException extends RuntimeException {
    public SelfSubscriptionException(String message) {
        super(message);
    }
}
