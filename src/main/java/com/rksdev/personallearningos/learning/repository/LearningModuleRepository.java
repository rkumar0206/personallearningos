package com.rksdev.personallearningos.learning.repository;

import com.rksdev.personallearningos.learning.model.LearningModuleEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface LearningModuleRepository extends JpaRepository<LearningModuleEntity, Long> {

    @Query("SELECT m FROM LearningModuleEntity m WHERE m.id = :id AND m.path.user.id = :userId")
    Optional<LearningModuleEntity> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT m FROM LearningModuleEntity m WHERE m.path.id = :pathId AND m.path.user.id = :userId order by m.updatedAt desc")
    List<LearningModuleEntity> findAllByPathIdAndUserId(Long pathId, Long userId);

    @Query("""
                SELECT m FROM LearningModuleEntity m
                WHERE m.path.id = :pathId
                  AND m.path.user.id = :userId
                  AND (CAST(:search AS string) IS NULL OR LOWER(m.title) LIKE :search)
                  AND (
                    CAST(:lastUpdatedAt AS instant) IS NULL OR
                    m.updatedAt < :lastUpdatedAt OR
                    (m.updatedAt = :lastUpdatedAt AND m.id < :lastId)
                  )
                ORDER BY m.updatedAt DESC, m.id DESC
            """)
    List<LearningModuleEntity> findPaginatedModules(
            @Param("pathId") Long pathId,
            @Param("userId") Long userId,
            @Param("search") String search,
            @Param("lastUpdatedAt") Instant lastUpdatedAt,
            @Param("lastId") Long lastId,
            Pageable pageable
    );

    @Query("SELECT lm FROM LearningModuleEntity lm where lm.title ILIKE :title and lm.path.id = :pathId")
    Optional<LearningModuleEntity> findByTitleAndPathId(String title, Long pathId);

    boolean existsByIdAndPathUserId(Long id, Long userId);
}
