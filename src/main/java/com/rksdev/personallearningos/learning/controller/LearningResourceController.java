package com.rksdev.personallearningos.learning.controller;

import com.rksdev.personallearningos.learning.dtos.LearningResourceRequestDto;
import com.rksdev.personallearningos.learning.dtos.LearningResourceResponseDto;
import com.rksdev.personallearningos.learning.service.LearningResourceService;
import com.rksdev.security.web.CurrentUserId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
public class LearningResourceController {

    private final LearningResourceService learningResourceService;

    @PostMapping("/topic/{topicId}")
    public ResponseEntity<LearningResourceResponseDto> createResource(
            @CurrentUserId Long userId,
            @PathVariable Long topicId,
            @RequestBody @Valid LearningResourceRequestDto resourceData) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(learningResourceService.createResource(userId, topicId, resourceData));
    }

    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<LearningResourceResponseDto>> getAllResourcesForTopic(
            @CurrentUserId Long userId,
            @PathVariable Long topicId) {
        return ResponseEntity.ok(learningResourceService.getAllResourcesForTopic(userId, topicId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LearningResourceResponseDto> getResourceById(
            @CurrentUserId Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(learningResourceService.getResourceById(userId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LearningResourceResponseDto> updateResource(
            @CurrentUserId Long userId,
            @PathVariable Long id,
            @RequestBody @Valid LearningResourceRequestDto updatedData) {
        return ResponseEntity.ok(learningResourceService.updateResource(userId, id, updatedData));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(
            @CurrentUserId Long userId,
            @PathVariable Long id) {
        learningResourceService.deleteResource(userId, id);
        return ResponseEntity.noContent().build();
    }
}