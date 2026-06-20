package com.rksdev.personallearningos.learning.repository;

import com.rksdev.personallearningos.learning.model.LearningResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningResourceRepository extends JpaRepository<LearningResourceEntity, Long> {

    @Query("SELECT r FROM LearningResourceEntity r WHERE r.id = :id AND r.topic.module.path.user.id = :userId")
    Optional<LearningResourceEntity> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT r FROM LearningResourceEntity r WHERE r.topic.id = :topicId AND r.topic.module.path.user.id = :userId")
    List<LearningResourceEntity> findAllByTopicIdAndUserId(Long topicId, Long userId);
}
