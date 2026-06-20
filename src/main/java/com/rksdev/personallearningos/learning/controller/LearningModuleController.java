package com.rksdev.personallearningos.learning.controller;

import com.rksdev.personallearningos.learning.dtos.LearningModuleRequestDto;
import com.rksdev.personallearningos.learning.dtos.LearningModuleResponseDto;
import com.rksdev.personallearningos.learning.service.LearningModuleService;
import com.rksdev.security.web.CurrentUserId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
public class LearningModuleController {

    private final LearningModuleService learningModuleService;

    @PostMapping("/path/{pathId}")
    public ResponseEntity<LearningModuleResponseDto> createModule(
            @CurrentUserId Long userId,
            @PathVariable Long pathId,
            @RequestBody @Valid LearningModuleRequestDto moduleData) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(learningModuleService.createModule(userId, pathId, moduleData));
    }

    @GetMapping("/path/{pathId}")
    public ResponseEntity<List<LearningModuleResponseDto>> getAllModulesForPath(
            @CurrentUserId Long userId,
            @PathVariable Long pathId) {
        return ResponseEntity.ok(learningModuleService.getAllModulesForPath(userId, pathId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LearningModuleResponseDto> getModuleById(
            @CurrentUserId Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(learningModuleService.getModuleById(userId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LearningModuleResponseDto> updateModule(
            @CurrentUserId Long userId,
            @PathVariable Long id,
            @RequestBody @Valid LearningModuleRequestDto updatedData) {
        return ResponseEntity.ok(learningModuleService.updateModule(userId, id, updatedData));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModule(
            @CurrentUserId Long userId,
            @PathVariable Long id) {
        learningModuleService.deleteModule(userId, id);
        return ResponseEntity.noContent().build();
    }
}