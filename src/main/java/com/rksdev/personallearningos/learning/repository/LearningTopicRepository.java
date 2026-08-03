package com.rksdev.personallearningos.learning.repository;

import com.rksdev.personallearningos.learning.model.LearningTopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningTopicRepository extends JpaRepository<LearningTopicEntity, Long> {

    @Query("SELECT t FROM LearningTopicEntity t WHERE t.id = :id AND t.module.path.user.id = :userId")
    Optional<LearningTopicEntity> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT t FROM LearningTopicEntity t WHERE t.module.id = :moduleId AND t.module.path.user.id = :userId order by t.updatedAt desc")
    List<LearningTopicEntity> findAllByModuleIdAndUserId(Long moduleId, Long userId);

    boolean existsByIdAndModulePathUserId(Long id, Long userId);

    @Query("select lt from LearningTopicEntity lt where lt.title ilike :title and lt.module.id = :moduleId")
    Optional<LearningTopicEntity> findByTitleAndModuleId(String title, Long moduleId);
}
