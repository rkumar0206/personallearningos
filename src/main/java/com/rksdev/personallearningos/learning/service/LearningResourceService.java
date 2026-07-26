package com.rksdev.personallearningos.learning.service;

import com.rksdev.personallearningos.learning.dtos.LearningResourceRequestDto;
import com.rksdev.personallearningos.learning.dtos.LearningResourceResponseDto;
import com.rksdev.personallearningos.learning.mapper.LearningResourceMapper;
import com.rksdev.personallearningos.learning.model.LearningResourceEntity;
import com.rksdev.personallearningos.learning.model.LearningTopicEntity;
import com.rksdev.personallearningos.learning.repository.LearningResourceRepository;
import com.rksdev.personallearningos.learning.repository.LearningTopicRepository;
import com.rksdev.personallearningos.shared.exception.ResourceNotFoundInDbException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LearningResourceService {

    private final LearningResourceRepository learningResourceRepository;
    private final LearningTopicRepository learningTopicRepository;
    private final LearningResourceMapper learningResourceMapper;
    private final ResourceOwnershipService ownershipService;

    @Transactional
    public LearningResourceResponseDto createResource(Long userId, Long topicId, LearningResourceRequestDto resourceData) {
        // Verify parent boundary ownership before creation
        ownershipService.verifyTopicOwnership(userId, topicId);

        LearningTopicEntity topic = learningTopicRepository.getReferenceById(topicId);

        LearningResourceEntity entity = learningResourceMapper.toEntity(resourceData);
        entity.setTopic(topic);

        return learningResourceMapper.toResponseDto(learningResourceRepository.save(entity));
    }

    public List<LearningResourceResponseDto> getAllResourcesForTopic(Long userId, Long topicId) {
        return learningResourceMapper.toResponseDtoList(
                learningResourceRepository.findAllByTopicIdAndUserId(topicId, userId)
        );
    }

    public LearningResourceResponseDto getResourceById(Long userId, Long resourceId) {
        return learningResourceRepository.findByIdAndUserId(resourceId, userId)
                .map(learningResourceMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundInDbException("Learning Resource not found or unauthorized"));
    }

    @Transactional
    public LearningResourceResponseDto updateResource(Long userId, Long resourceId, LearningResourceRequestDto updatedData) {
        LearningResourceEntity existingResource = learningResourceRepository.findByIdAndUserId(resourceId, userId)
                .orElseThrow(() -> new ResourceNotFoundInDbException("Learning Resource not found or unauthorized"));

        learningResourceMapper.updateEntityFromDto(updatedData, existingResource);
        return learningResourceMapper.toResponseDto(learningResourceRepository.saveAndFlush(existingResource));
    }

    @Transactional
    public void deleteResource(Long userId, Long resourceId) {
        LearningResourceEntity existingResource = learningResourceRepository.findByIdAndUserId(resourceId, userId)
                .orElseThrow(() -> new ResourceNotFoundInDbException("Learning Resource not found or unauthorized"));

        learningResourceRepository.delete(existingResource);
    }
}