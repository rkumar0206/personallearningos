package com.rksdev.personallearningos.learning.repository;

import com.rksdev.personallearningos.learning.model.LearningPathEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningPathRepository extends JpaRepository<LearningPathEntity, Long> {
    @Query("select lp from LearningPathEntity lp where lp.user.id = :userId")
    List<LearningPathEntity> findAllByUserId(Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);

    @Query("SELECT lp FROM LearningPathEntity lp where lp.title ILIKE :title and lp.user.id = :userId")
    Optional<LearningPathEntity> findByTitleAndUserId(String title, Long userId);

    Optional<LearningPathEntity> findByIdAndUserId(Long id, Long userId);
}
