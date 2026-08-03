package com.rksdev.personallearningos.learning.repository;

import com.rksdev.personallearningos.learning.model.LearningTopicEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface LearningTopicRepository extends JpaRepository<LearningTopicEntity, Long> {

    @Query("SELECT t FROM LearningTopicEntity t WHERE t.id = :id AND t.module.path.user.id = :userId")
    Optional<LearningTopicEntity> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT t FROM LearningTopicEntity t WHERE t.module.id = :moduleId AND t.module.path.user.id = :userId order by t.updatedAt desc")
    List<LearningTopicEntity> findAllByModuleIdAndUserId(Long moduleId, Long userId);

    @Query("""
                SELECT t FROM LearningTopicEntity t
                WHERE t.module.id = :moduleId
                  AND t.module.path.user.id = :userId
                  AND (CAST(:search AS string) IS NULL OR LOWER(t.title) LIKE :search)
                  AND (
                    CAST(:lastUpdatedAt AS instant) IS NULL OR
                    t.updatedAt < :lastUpdatedAt OR
                    (t.updatedAt = :lastUpdatedAt AND t.id < :lastId)
                  )
                ORDER BY t.updatedAt DESC, t.id DESC
            """)
    List<LearningTopicEntity> findPaginatedTopics(
            @Param("moduleId") Long moduleId,
            @Param("userId") Long userId,
            @Param("search") String search,
            @Param("lastUpdatedAt") Instant lastUpdatedAt,
            @Param("lastId") Long lastId,
            Pageable pageable
    );

    boolean existsByIdAndModulePathUserId(Long id, Long userId);

    @Query("select lt from LearningTopicEntity lt where lt.title ilike :title and lt.module.id = :moduleId")
    Optional<LearningTopicEntity> findByTitleAndModuleId(String title, Long moduleId);
}
