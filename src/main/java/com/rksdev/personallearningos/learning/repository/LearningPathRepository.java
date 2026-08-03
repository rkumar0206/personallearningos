package com.rksdev.personallearningos.learning.repository;

import com.rksdev.personallearningos.learning.model.LearningPathEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface LearningPathRepository extends JpaRepository<LearningPathEntity, Long> {


    @Query("select lp from LearningPathEntity lp where lp.user.id = :userId order by lp.updatedAt desc")
    List<LearningPathEntity> findAllByUserId(Long userId);

    @Query("""
        SELECT lp FROM LearningPathEntity lp
        WHERE lp.user.id = :userId
          AND (CAST(:search AS string) IS NULL OR LOWER(lp.title) LIKE :search)
          AND (
            CAST(:lastUpdatedAt AS instant) IS NULL OR
            lp.updatedAt < :lastUpdatedAt OR
            (lp.updatedAt = :lastUpdatedAt AND lp.id < :lastId)
          )
        ORDER BY lp.updatedAt DESC, lp.id DESC
    """)
    List<LearningPathEntity> findByUserIdWithCursorAndSearch(
            @Param("userId") Long userId,
            @Param("search") String search,
            @Param("lastUpdatedAt") Instant lastUpdatedAt,
            @Param("lastId") Long lastId,
            Pageable pageable
    );

    boolean existsByIdAndUserId(Long id, Long userId);

    @Query("SELECT lp FROM LearningPathEntity lp where lp.title ILIKE :title and lp.user.id = :userId")
    Optional<LearningPathEntity> findByTitleAndUserId(String title, Long userId);

    Optional<LearningPathEntity> findByIdAndUserId(Long id, Long userId);
}
