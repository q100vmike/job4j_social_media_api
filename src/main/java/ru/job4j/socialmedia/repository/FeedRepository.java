package ru.job4j.socialmedia.repository;

import ru.job4j.socialmedia.entity.PostEntity;

import java.util.List;
import java.util.UUID;

public interface FeedRepository {
    List<PostEntity> findFeed(UUID userId, int limit, int offset);
}
