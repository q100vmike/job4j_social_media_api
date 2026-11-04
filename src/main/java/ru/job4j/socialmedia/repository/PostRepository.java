package ru.job4j.socialmedia.repository;

import ru.job4j.socialmedia.entity.PostEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository {

    CreatePostResponse createPost(
            CreatePostRequest request
    );

    boolean markAsDeleted(UUID id, Instant updatedAt);

    Optional<CreatePostResponse> updatePost(
            UpdatePostRequest request
    );

    record CreatePostRequest(
            UUID id,
            UUID authorId,
            String title,
            String content,
            PostEntity.Status status,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

    record CreatePostResponse(
            UUID id,
            UUID authorId,
            String title,
            String content,
            PostEntity.Status status,
            Instant createdAt,
            Instant updatedAt
    ) {
    }

    record UpdatePostRequest(
            UUID id,
            String title,
            String content,
            Instant updatedAt
    ) {
    }
}