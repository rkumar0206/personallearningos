package com.rksdev.personallearningos.learning.service;

import com.rksdev.personallearningos.learning.repository.LearningModuleRepository;
import com.rksdev.personallearningos.learning.repository.LearningPathRepository;
import com.rksdev.personallearningos.learning.repository.LearningTopicRepository;
import com.rksdev.personallearningos.shared.exception.ResourceAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResourceOwnershipService {

    private final LearningPathRepository learningPathRepository;
    private final LearningModuleRepository learningModuleRepository;
    private final LearningTopicRepository learningTopicRepository;

    public void verifyPathOwnership(Long userId, Long pathId) {
        if (!learningPathRepository.existsByIdAndUserId(pathId, userId)) {
            throw new ResourceAccessException("Unauthorized: You do not own this Learning Path.");
        }
    }

    public void verifyModuleOwnership(Long userId, Long moduleId) {
        if (!learningModuleRepository.existsByIdAndPathUserId(moduleId, userId)) {
            throw new ResourceAccessException("Unauthorized: You do not own this Learning Module.");
        }
    }

    public void verifyTopicOwnership(Long userId, Long topicId) {
        if (!learningTopicRepository.existsByIdAndModulePathUserId(topicId, userId)) {
            throw new ResourceAccessException("Unauthorized: You do not own this Learning Topic.");
        }
    }
}