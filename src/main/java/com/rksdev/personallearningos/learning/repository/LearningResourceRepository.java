package com.rksdev.personallearningos.learning.repository;

import com.rksdev.personallearningos.learning.model.LearningResourceEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface LearningResourceRepository extends JpaRepository<LearningResourceEntity, Long> {

    @Query("SELECT r FROM LearningResourceEntity r WHERE r.id = :id AND r.topic.module.path.user.id = :userId")
    Optional<LearningResourceEntity> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT r FROM LearningResourceEntity r WHERE r.topic.id = :topicId AND r.topic.module.path.user.id = :userId order by r.updatedAt desc")
    List<LearningResourceEntity> findAllByTopicIdAndUserId(Long topicId, Long userId);

    @Query("""
        SELECT r FROM LearningResourceEntity r
        WHERE r.topic.id = :topicId
          AND r.topic.module.path.user.id = :userId
          AND (
            CAST(:search AS string) IS NULL OR (
              LOWER(r.title) LIKE :search OR
              LOWER(r.content) LIKE :search OR
              LOWER(r.urlDescription) LIKE :search
            )
          )
          AND (
            CAST(:lastUpdatedAt AS instant) IS NULL OR
            r.updatedAt < :lastUpdatedAt OR
            (r.updatedAt = :lastUpdatedAt AND r.id < :lastId)
          )
        ORDER BY r.updatedAt DESC, r.id DESC
    """)
    List<LearningResourceEntity> findPaginatedResources(
            @Param("topicId") Long topicId,
            @Param("userId") Long userId,
            @Param("search") String search,
            @Param("lastUpdatedAt") Instant lastUpdatedAt,
            @Param("lastId") Long lastId,
            Pageable pageable
    );
}
