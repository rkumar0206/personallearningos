package com.rksdev.personallearningos.learning.repository;

import com.rksdev.personallearningos.learning.model.LearningModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningModuleRepository extends JpaRepository<LearningModuleEntity, Long> {

    @Query("SELECT m FROM LearningModuleEntity m WHERE m.id = :id AND m.path.user.id = :userId")
    Optional<LearningModuleEntity> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT m FROM LearningModuleEntity m WHERE m.path.id = :pathId AND m.path.user.id = :userId")
    List<LearningModuleEntity> findAllByPathIdAndUserId(Long pathId, Long userId);

    @Query("SELECT lm FROM LearningModuleEntity lm where lm.title ILIKE :title and lm.path.id = :pathId")
    Optional<LearningModuleEntity> findByTitleAndPathId(String title, Long pathId);

    boolean existsByIdAndPathUserId(Long id, Long userId);
}
