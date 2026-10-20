package ru.job4j.socialmedia.repository;

import java.time.Instant;
import java.util.UUID;

public interface PostRepository {

    CreatePostResponse createPost(
            CreatePostRequest request
    );

    record CreatePostRequest(
            UUID id,
            UUID authorId,
            String title,
            String content,
            Status status,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

    record CreatePostResponse(
            UUID id,
            UUID authorId,
            String title,
            String content,
            Status status,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

    enum Status {
        PUBLISHED,
        DELETED
    }
}