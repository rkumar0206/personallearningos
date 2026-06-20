package com.rksdev.personallearningos.learning.controller;

import com.rksdev.personallearningos.learning.dtos.LearningPathRequestDto;
import com.rksdev.personallearningos.learning.dtos.LearningPathResponseDto;
import com.rksdev.personallearningos.learning.service.LearningPathService;
import com.rksdev.security.web.CurrentUserId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/learning-paths")
@RequiredArgsConstructor
public class LearningPathController {

    private final LearningPathService learningPathService;

    @PostMapping
    public ResponseEntity<LearningPathResponseDto> createPath(
            @CurrentUserId Long userId,
            @RequestBody @Valid LearningPathRequestDto pathData) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(learningPathService.createPath(userId, pathData));
    }

    @GetMapping
    public ResponseEntity<List<LearningPathResponseDto>> getAllPaths(@CurrentUserId Long userId) {
        return ResponseEntity.ok(learningPathService.getAllPathsForUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LearningPathResponseDto> getPathById(
            @CurrentUserId Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(learningPathService.getPathById(userId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LearningPathResponseDto> updatePath(
            @CurrentUserId Long userId,
            @PathVariable Long id,
            @RequestBody @Valid LearningPathRequestDto updatedData) {
        return ResponseEntity.ok(learningPathService.updatePath(userId, id, updatedData));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePath(
            @CurrentUserId Long userId,
            @PathVariable Long id) {
        learningPathService.deletePath(userId, id);
        return ResponseEntity.noContent().build();
    }
}