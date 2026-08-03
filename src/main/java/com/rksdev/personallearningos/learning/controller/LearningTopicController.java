package com.rksdev.personallearningos.learning.controller;

import com.rksdev.personallearningos.learning.dtos.CursorPageResponse;
import com.rksdev.personallearningos.learning.dtos.LearningModuleResponseDto;
import com.rksdev.personallearningos.learning.dtos.LearningTopicRequestDto;
import com.rksdev.personallearningos.learning.dtos.LearningTopicResponseDto;
import com.rksdev.personallearningos.learning.service.LearningTopicService;
import com.rksdev.security.web.CurrentUserId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/topics")
@RequiredArgsConstructor
public class LearningTopicController {

    private final LearningTopicService learningTopicService;

    @PostMapping("/module/{moduleId}")
    public ResponseEntity<LearningTopicResponseDto> createTopic(
            @CurrentUserId Long userId,
            @PathVariable Long moduleId,
            @RequestBody @Valid LearningTopicRequestDto topicData) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(learningTopicService.createTopic(userId, moduleId, topicData));
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<LearningTopicResponseDto>> getAllTopicsForModule(
            @CurrentUserId Long userId,
            @PathVariable Long moduleId) {
        return ResponseEntity.ok(learningTopicService.getAllTopicsForModule(userId, moduleId));
    }

    @GetMapping("/paginated/module/{moduleId}")
    public ResponseEntity<CursorPageResponse<LearningTopicResponseDto>> getAllTopicsForModule(
            @CurrentUserId Long userId,
            @PathVariable Long moduleId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(learningTopicService.getPaginatedTopics(moduleId, userId, search, cursor, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LearningTopicResponseDto> getTopicById(
            @CurrentUserId Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(learningTopicService.getTopicById(userId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LearningTopicResponseDto> updateTopic(
            @CurrentUserId Long userId,
            @PathVariable Long id,
            @RequestBody @Valid LearningTopicRequestDto updatedData) {
        return ResponseEntity.ok(learningTopicService.updateTopic(userId, id, updatedData));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(
            @CurrentUserId Long userId,
            @PathVariable Long id) {
        learningTopicService.deleteTopic(userId, id);
        return ResponseEntity.noContent().build();
    }
}