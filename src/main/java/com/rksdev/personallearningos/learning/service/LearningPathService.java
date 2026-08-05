package com.rksdev.personallearningos.learning.service;

import com.rksdev.personallearningos.learning.dtos.CursorPageResponse;
import com.rksdev.personallearningos.learning.dtos.LearningPathRequestDto;
import com.rksdev.personallearningos.learning.dtos.LearningPathResponseDto;
import com.rksdev.personallearningos.learning.dtos.PathModuleCountDto;
import com.rksdev.personallearningos.learning.mapper.LearningPathMapper;
import com.rksdev.personallearningos.learning.model.LearningPathEntity;
import com.rksdev.personallearningos.learning.repository.LearningModuleRepository;
import com.rksdev.personallearningos.learning.repository.LearningPathRepository;
import com.rksdev.personallearningos.learning.util.CursorUtils;
import com.rksdev.personallearningos.shared.exception.DuplicateResourceInDbException;
import com.rksdev.personallearningos.shared.exception.ResourceNotFoundInDbException;
import com.rksdev.personallearningos.shared.util.AppUtils;
import com.rksdev.personallearningos.user.model.UserEntity;
import com.rksdev.personallearningos.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LearningPathService {

    private final LearningPathRepository learningPathRepository;
    private final LearningModuleRepository learningModuleRepository;
    private final UserRepository userRepository;
    private final LearningPathMapper learningPathMapper;

    @Transactional
    public LearningPathResponseDto createPath(Long userId, LearningPathRequestDto pathData) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundInDbException("User not found with ID: " + userId));

        LearningPathEntity entity = learningPathMapper.toEntity(pathData);

        if (isDuplicateExists(entity.getTitle(), userId)) {
            throw new DuplicateResourceInDbException("Learning path already exists");
        }

        entity.setUser(user);

        return learningPathMapper.toResponseDto(learningPathRepository.save(entity));
    }

    private boolean isDuplicateExists(String title, Long userId) {
        return learningPathRepository.findByTitleAndUserId(title, userId)
                .isPresent();

    }

    public List<LearningPathResponseDto> getAllPathsForUser(Long userId) {
        List<LearningPathResponseDto> responseDtoList = learningPathMapper.toResponseDtoList(learningPathRepository.findAllByUserId(userId));
        addModuleCountToResponseDtoList(responseDtoList, userId);
        return responseDtoList;
    }

    public CursorPageResponse<LearningPathResponseDto> getLearningPaths(
            Long userId,
            String search,
            String cursorToken,
            int limit
    ) {
        // Decode cursor
        CursorUtils.Cursor cursor = CursorUtils.decode(cursorToken);
        var lastUpdatedAt = cursor != null ? cursor.updatedAt() : null;
        var lastId = cursor != null ? cursor.id() : null;

        // Clean search input
        String cleanSearch = AppUtils.getSearchStringWithPattern(search);

        // Fetch limit + 1 to check for hasNext
        List<LearningPathEntity> results = learningPathRepository.findByUserIdWithCursorAndSearch(
                userId,
                cleanSearch,
                lastUpdatedAt,
                lastId,
                PageRequest.of(0, limit + 1)
        );

        boolean hasNext = results.size() > limit;
        List<LearningPathEntity> pageData = hasNext ? results.subList(0, limit) : results;

        // Generate next cursor from last element on current page
        String nextCursor = null;
        if (hasNext && !pageData.isEmpty()) {
            LearningPathEntity lastItem = pageData.getLast();
            nextCursor = CursorUtils.encode(lastItem.getUpdatedAt(), lastItem.getId());
        }

        List<LearningPathResponseDto> responseDtoList = learningPathMapper.toResponseDtoList(pageData);

        addModuleCountToResponseDtoList(responseDtoList, userId);

        return new CursorPageResponse<>(responseDtoList, nextCursor, hasNext);
    }

    private void addModuleCountToResponseDtoList(List<LearningPathResponseDto> responseDtoList, Long userId) {

        List<Long> pathIds = responseDtoList.stream().map(LearningPathResponseDto::getId).toList();
        Map<Long, Long> countMap = learningModuleRepository.countModulesByPathIdsDto(pathIds, userId)
                .stream().collect(Collectors.toMap(
                        PathModuleCountDto::pathId,
                        PathModuleCountDto::count
                ));

        responseDtoList.forEach(responseDto -> {
            responseDto.setModulesCount(countMap.get(responseDto.getId()));
        });
    }

    public CursorPageResponse<LearningPathResponseDto> getLearningPaths(
            Long userId,
            String search,
            String cursorToken,
            int limit
    ) {
        // Decode cursor
        CursorUtils.Cursor cursor = CursorUtils.decode(cursorToken);
        var lastUpdatedAt = cursor != null ? cursor.updatedAt() : null;
        var lastId = cursor != null ? cursor.id() : null;

        // Clean search input
        String cleanSearch = AppUtils.getSearchStringWithPattern(search);

        // Fetch limit + 1 to check for hasNext
        List<LearningPathEntity> results = learningPathRepository.findByUserIdWithCursorAndSearch(
                userId,
                cleanSearch,
                lastUpdatedAt,
                lastId,
                PageRequest.of(0, limit + 1)
        );

        boolean hasNext = results.size() > limit;
        List<LearningPathEntity> pageData = hasNext ? results.subList(0, limit) : results;

        // Generate next cursor from last element on current page
        String nextCursor = null;
        if (hasNext && !pageData.isEmpty()) {
            LearningPathEntity lastItem = pageData.getLast();
            nextCursor = CursorUtils.encode(lastItem.getUpdatedAt(), lastItem.getId());
        }

        return new CursorPageResponse<>(learningPathMapper.toResponseDtoList(pageData), nextCursor, hasNext);
    }

    public LearningPathResponseDto getPathById(Long userId, Long pathId) {
        return learningPathRepository.findByIdAndUserId(pathId, userId)
                .map(learningPathMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundInDbException("Learning Path not found"));
    }

    @Transactional
    public LearningPathResponseDto updatePath(Long userId, Long pathId, LearningPathRequestDto updatedData) {

        LearningPathEntity existingPath = learningPathRepository.findByIdAndUserId(pathId, userId)
                .orElseThrow(() -> new ResourceNotFoundInDbException("Learning Path not found"));

        if (!existingPath.getTitle().equals(updatedData.getTitle()) && isDuplicateExists(updatedData.getTitle(), userId)) {
            throw new DuplicateResourceInDbException("Learning path already exists");
        }

        existingPath.setTitle(updatedData.getTitle());
        existingPath.setDescription(updatedData.getDescription());
        existingPath.setStatus(updatedData.getStatus());

        return learningPathMapper.toResponseDto(learningPathRepository.saveAndFlush(existingPath));
    }

    @Transactional
    public void deletePath(Long userId, Long pathId) {
        if (!learningPathRepository.existsByIdAndUserId(pathId, userId)) {
            throw new ResourceNotFoundInDbException("Learning Path not found");
        }
        learningPathRepository.deleteById(pathId);
    }
}
