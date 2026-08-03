package com.rksdev.personallearningos.learning.service;

import com.rksdev.personallearningos.learning.dtos.CursorPageResponse;
import com.rksdev.personallearningos.learning.dtos.LearningResourceRequestDto;
import com.rksdev.personallearningos.learning.dtos.LearningResourceResponseDto;
import com.rksdev.personallearningos.learning.mapper.LearningResourceMapper;
import com.rksdev.personallearningos.learning.model.LearningResourceEntity;
import com.rksdev.personallearningos.learning.model.LearningTopicEntity;
import com.rksdev.personallearningos.learning.repository.LearningResourceRepository;
import com.rksdev.personallearningos.learning.repository.LearningTopicRepository;
import com.rksdev.personallearningos.learning.util.CursorUtils;
import com.rksdev.personallearningos.shared.exception.ResourceNotFoundInDbException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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

    @Transactional(readOnly = true)
    public CursorPageResponse<LearningResourceResponseDto> getPaginatedResources(
            Long topicId,
            Long userId,
            String search,
            String cursor,
            int limit) {

        // 1. Format search pattern safely in Java for multi-field wildcards
        String searchPattern = (search != null && !search.trim().isEmpty())
                ? "%" + search.trim().toLowerCase() + "%"
                : null;

        // 2. Decode cursor using your CursorUtils
        Instant lastUpdatedAt = null;
        Long lastId = null;

        if (cursor != null && !cursor.isBlank()) {
            CursorUtils.Cursor decoded = CursorUtils.decode(cursor);
            if (decoded != null) {
                lastUpdatedAt = decoded.updatedAt();
                lastId = decoded.id();
            }
        }

        // 3. Fetch limit + 1 to determine if a next page exists
        Pageable pageable = PageRequest.of(0, limit + 1);

        List<LearningResourceEntity> resources = learningResourceRepository.findPaginatedResources(
                topicId, userId, searchPattern, lastUpdatedAt, lastId, pageable
        );

        // 4. Calculate pagination metadata (hasNext, nextCursor)
        boolean hasNext = resources.size() > limit;
        List<LearningResourceEntity> resultList = hasNext ? resources.subList(0, limit) : resources;

        String nextCursor = null;
        if (hasNext && !resultList.isEmpty()) {
            LearningResourceEntity lastItem = resultList.getLast();
            nextCursor = CursorUtils.encode(lastItem.getUpdatedAt(), lastItem.getId());
        }
        return new CursorPageResponse<>(learningResourceMapper.toResponseDtoList(resultList), nextCursor, hasNext);
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