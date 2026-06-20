package com.rksdev.personallearningos.learning.service;

import com.rksdev.personallearningos.learning.dtos.LearningTopicRequestDto;
import com.rksdev.personallearningos.learning.dtos.LearningTopicResponseDto;
import com.rksdev.personallearningos.learning.mapper.LearningTopicMapper;
import com.rksdev.personallearningos.learning.model.LearningModuleEntity;
import com.rksdev.personallearningos.learning.model.LearningTopicEntity;
import com.rksdev.personallearningos.learning.repository.LearningModuleRepository;
import com.rksdev.personallearningos.learning.repository.LearningTopicRepository;
import com.rksdev.personallearningos.shared.exception.DuplicateResourceInDbException;
import com.rksdev.personallearningos.shared.exception.ResourceNotFoundInDbException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LearningTopicService {

    private final LearningTopicRepository learningTopicRepository;
    private final LearningModuleRepository learningModuleRepository;
    private final LearningTopicMapper learningTopicMapper;
    private final ResourceOwnershipService ownershipService;

    @Transactional
    public LearningTopicResponseDto createTopic(Long userId, Long moduleId, LearningTopicRequestDto topicData) {
        // Verify parent boundary ownership before creation
        ownershipService.verifyModuleOwnership(userId, moduleId);

        LearningModuleEntity module = learningModuleRepository.getReferenceById(moduleId);

        LearningTopicEntity entity = learningTopicMapper.toEntity(topicData);
        entity.setModule(module);

        if (isDuplicateExists(entity.getTitle(), module.getId())) {
            throw new DuplicateResourceInDbException("This title already exists in this module.");
        }

        return learningTopicMapper.toResponseDto(learningTopicRepository.save(entity));
    }

    private boolean isDuplicateExists(String title, Long moduleId) {
        return learningTopicRepository.findByTitleAndModuleId(title, moduleId).isPresent();
    }

    public List<LearningTopicResponseDto> getAllTopicsForModule(Long userId, Long moduleId) {
        return learningTopicMapper.toResponseDtoList(
                learningTopicRepository.findAllByModuleIdAndUserId(moduleId, userId)
        );
    }

    public LearningTopicResponseDto getTopicById(Long userId, Long topicId) {
        return learningTopicRepository.findByIdAndUserId(topicId, userId)
                .map(learningTopicMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundInDbException("Learning Topic not found or unauthorized"));
    }

    @Transactional
    public LearningTopicResponseDto updateTopic(Long userId, Long topicId, LearningTopicRequestDto updatedData) {
        LearningTopicEntity existingTopic = learningTopicRepository.findByIdAndUserId(topicId, userId)
                .orElseThrow(() -> new ResourceNotFoundInDbException("Learning Topic not found or unauthorized"));

        if (!existingTopic.getTitle().equals(updatedData.getTitle()) && isDuplicateExists(updatedData.getTitle(), existingTopic.getId())) {
            throw new DuplicateResourceInDbException("This title already exists in this module.");
        }

        existingTopic.setTitle(updatedData.getTitle());
        existingTopic.setStatus(updatedData.getStatus());
        existingTopic.setDisplayOrder(updatedData.getDisplayOrder());

        return learningTopicMapper.toResponseDto(learningTopicRepository.saveAndFlush(existingTopic));
    }

    @Transactional
    public void deleteTopic(Long userId, Long topicId) {
        LearningTopicEntity existingTopic = learningTopicRepository.findByIdAndUserId(topicId, userId)
                .orElseThrow(() -> new ResourceNotFoundInDbException("Learning Topic not found or unauthorized"));

        learningTopicRepository.delete(existingTopic);
    }
}