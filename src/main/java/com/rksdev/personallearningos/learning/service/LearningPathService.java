package com.rksdev.personallearningos.learning.service;

import com.rksdev.personallearningos.learning.dtos.LearningPathRequestDto;
import com.rksdev.personallearningos.learning.dtos.LearningPathResponseDto;
import com.rksdev.personallearningos.shared.exception.DuplicateResourceInDbException;
import com.rksdev.personallearningos.shared.exception.ResourceNotFoundInDbException;
import com.rksdev.personallearningos.learning.mapper.LearningPathMapper;
import com.rksdev.personallearningos.learning.model.LearningPathEntity;
import com.rksdev.personallearningos.learning.repository.LearningPathRepository;
import com.rksdev.personallearningos.user.model.UserEntity;
import com.rksdev.personallearningos.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LearningPathService {

    private final LearningPathRepository learningPathRepository;
    private final UserRepository userRepository;
    private final LearningPathMapper learningPathMapper;

    @Transactional
    public LearningPathResponseDto createPath(Long userId, LearningPathRequestDto pathData) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundInDbException("User not found with ID: " + userId));

        LearningPathEntity entity = learningPathMapper.toEntity(pathData);

        if (isDuplicateExists(entity.getTitle(), entity.getUser().getId())) {
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
        return learningPathMapper.toResponseDtoList(learningPathRepository.findAllByUserId(userId));
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

        if (!existingPath.getTitle().equals(updatedData.getTitle()) && isDuplicateExists(updatedData.getTitle(), existingPath.getUser().getId())) {
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
