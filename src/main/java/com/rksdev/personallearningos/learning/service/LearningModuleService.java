package com.rksdev.personallearningos.learning.service;

import com.rksdev.personallearningos.learning.dtos.CursorPageResponse;
import com.rksdev.personallearningos.learning.dtos.LearningModuleRequestDto;
import com.rksdev.personallearningos.learning.dtos.LearningModuleResponseDto;
import com.rksdev.personallearningos.learning.mapper.LearningModuleMapper;
import com.rksdev.personallearningos.learning.model.LearningModuleEntity;
import com.rksdev.personallearningos.learning.model.LearningPathEntity;
import com.rksdev.personallearningos.learning.repository.LearningModuleRepository;
import com.rksdev.personallearningos.learning.repository.LearningPathRepository;
import com.rksdev.personallearningos.learning.util.CursorUtils;
import com.rksdev.personallearningos.shared.exception.DuplicateResourceInDbException;
import com.rksdev.personallearningos.shared.exception.ResourceNotFoundInDbException;
import com.rksdev.personallearningos.shared.util.AppUtils;
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
public class LearningModuleService {

    private final LearningModuleRepository learningModuleRepository;
    private final LearningPathRepository learningPathRepository;
    private final LearningModuleMapper learningModuleMapper;
    private final ResourceOwnershipService ownershipService;

    @Transactional
    public LearningModuleResponseDto createModule(Long userId, Long pathId, LearningModuleRequestDto moduleData) {
        // Verify parent boundary ownership before creation
        ownershipService.verifyPathOwnership(userId, pathId);

        LearningPathEntity path = learningPathRepository.getReferenceById(pathId);

        LearningModuleEntity entity = learningModuleMapper.toEntity(moduleData);

        if (isDuplicateExists(entity.getTitle(), entity.getPath().getId())) {
            throw new DuplicateResourceInDbException("Module already exists in this path");
        }

        entity.setPath(path);

        return learningModuleMapper.toResponseDto(learningModuleRepository.save(entity));
    }

    private boolean isDuplicateExists(String title, Long id) {
        return learningModuleRepository.findByTitleAndPathId(title, id).isPresent();
    }

    public List<LearningModuleResponseDto> getAllModulesForPath(Long userId, Long pathId) {
        return learningModuleMapper.toResponseDtoList(
                learningModuleRepository.findAllByPathIdAndUserId(pathId, userId)
        );
    }

    public CursorPageResponse<LearningModuleResponseDto> getPaginatedModules(
            Long pathId,
            Long userId,
            String search,
            String cursor,
            int limit
    ) {
        // 1. Format search pattern safely in Java to prevent JDBC type-casting issues
        String searchPattern = AppUtils.getSearchStringWithPattern(search);

        // 2. Decode cursor (e.g. encoded ISO-8601 Instant + ID)
        Instant lastUpdatedAt = null;
        Long lastId = null;

        if (cursor != null && !cursor.isBlank()) {
            CursorUtils.Cursor decoded = CursorUtils.decode(cursor);
            lastUpdatedAt = decoded.updatedAt();
            lastId = decoded.id();
        }

        // 3. Fetch limit + 1 to determine if there is a next page
        Pageable pageable = PageRequest.of(0, limit + 1);

        List<LearningModuleEntity> modules = learningModuleRepository.findPaginatedModules(
                pathId, userId, searchPattern, lastUpdatedAt, lastId, pageable
        );

        // 4. Calculate pagination metadata (hasNext, nextCursor)
        boolean hasNext = modules.size() > limit;
        List<LearningModuleEntity> resultList = hasNext ? modules.subList(0, limit) : modules;

        String nextCursor = null;
        if (hasNext && !resultList.isEmpty()) {
            LearningModuleEntity lastItem = resultList.getLast();
            nextCursor = CursorUtils.encode(lastItem.getUpdatedAt(), lastItem.getId());
        }

        return new CursorPageResponse<>(learningModuleMapper.toResponseDtoList(resultList), nextCursor, hasNext);
    }

    public LearningModuleResponseDto getModuleById(Long userId, Long moduleId) {
        return learningModuleRepository.findByIdAndUserId(moduleId, userId)
                .map(learningModuleMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundInDbException("Learning Module not found or unauthorized"));
    }

    @Transactional
    public LearningModuleResponseDto updateModule(Long userId, Long moduleId, LearningModuleRequestDto updatedData) {
        LearningModuleEntity existingModule = learningModuleRepository.findByIdAndUserId(moduleId, userId)
                .orElseThrow(() -> new ResourceNotFoundInDbException("Learning Module not found or unauthorized"));

        if (!existingModule.getTitle().equals(updatedData.getTitle()) && isDuplicateExists(updatedData.getTitle(), existingModule.getPath().getId())) {
            throw new DuplicateResourceInDbException("Module already exists in this path");
        }

        existingModule.setTitle(updatedData.getTitle());
        existingModule.setDisplayOrder(updatedData.getDisplayOrder());

        return learningModuleMapper.toResponseDto(learningModuleRepository.saveAndFlush(existingModule));
    }

    @Transactional
    public void deleteModule(Long userId, Long moduleId) {
        LearningModuleEntity existingModule = learningModuleRepository.findByIdAndUserId(moduleId, userId)
                .orElseThrow(() -> new ResourceNotFoundInDbException("Learning Module not found or unauthorized"));

        learningModuleRepository.delete(existingModule);
    }
}