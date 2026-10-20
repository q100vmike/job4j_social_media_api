package ru.job4j.socialmedia.entity;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class PostEntity {

    public enum Status {
        PUBLISHED,
        DELETED
    }

    private UUID id;
    private UUID authorId;
    private String title;
    private String content;
    private Status status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public PostEntity() {
    }

    public PostEntity(UUID id,
                      UUID authorId,
                      String title,
                      String content,
                      Status status,
                      OffsetDateTime createdAt,
                      OffsetDateTime updatedAt) {
        this.id = id;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public void setAuthorId(UUID authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PostEntity that = (PostEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return PostEntity.class.hashCode();
    }

    @Override
    public String toString() {
        return "PostEntity{"
              + "id=" + id
              + ", authorId=" + authorId
              + ", title='" + title + '\''
              + ", status=" + status
              + ", createdAt=" + createdAt
              + ", updatedAt=" + updatedAt
              + '}';
    }
}