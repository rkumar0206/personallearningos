package com.rksdev.personallearningos.import_export.service;

import com.rksdev.personallearningos.import_export.dtos.*;
import com.rksdev.personallearningos.import_export.exception.ImportExportErrorException;
import com.rksdev.personallearningos.learning.model.LearningModuleEntity;
import com.rksdev.personallearningos.learning.model.LearningPathEntity;
import com.rksdev.personallearningos.learning.model.LearningResourceEntity;
import com.rksdev.personallearningos.learning.model.LearningTopicEntity;
import com.rksdev.personallearningos.learning.model.enums.PathStatus;
import com.rksdev.personallearningos.learning.model.enums.TopicStatus;
import com.rksdev.personallearningos.learning.repository.LearningModuleRepository;
import com.rksdev.personallearningos.learning.repository.LearningPathRepository;
import com.rksdev.personallearningos.learning.repository.LearningResourceRepository;
import com.rksdev.personallearningos.learning.repository.LearningTopicRepository;
import com.rksdev.personallearningos.shared.exception.ResourceAccessException;
import com.rksdev.personallearningos.shared.service.EmailService;
import com.rksdev.personallearningos.user.model.UserEntity;
import com.rksdev.personallearningos.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDataImportService {

    private final UserRepository userRepository;
    private final LearningPathRepository pathRepository;
    private final LearningModuleRepository moduleRepository;
    private final LearningTopicRepository topicRepository;
    private final LearningResourceRepository resourceRepository;
    private final EmailService emailService;

    @Async
    @Transactional
    public void importUserDataAsync(Long currentUserId, UserDataExportDto importData, String email) {
        log.info("Starting async data import for email: {}", email);

        Optional<UserEntity> currentUserById = userRepository.findById(currentUserId);

        if (currentUserById.isPresent() && !currentUserById.get().getEmail().equals(email)) {
            throw new ResourceAccessException("Import failed, current user's email id does not match email id in the param");
        }

        ImportSummary summary = null;
        try {
            UserEntity user = userRepository.findByUsernameOrEmail(email, email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

            int pathsCreated = 0, modulesCreated = 0, topicsCreated = 0, resourcesCreated = 0, resourcesSkipped = 0;

            Long userId = user.getId();

            for (PathExportDto pathDto : importData.paths()) {
                // Path duplicate check: user_id + title (case-insensitive)
                Optional<LearningPathEntity> existingPathOpt = pathRepository
                        .findByTitleAndUserId(pathDto.title(), userId);

                LearningPathEntity path;
                if (existingPathOpt.isPresent()) {
                    path = existingPathOpt.get();
                } else {
                    path = LearningPathEntity.builder()
                            .user(user)
                            .title(pathDto.title())
                            .description(pathDto.description())
                            .status(pathDto.status() != null ? pathDto.status() : PathStatus.PLANNED)
                            .build();
                    path = pathRepository.save(path);
                    pathsCreated++;
                }

                if (pathDto.modules() == null) continue;

                for (ModuleExportDto moduleDto : pathDto.modules()) {
                    // Module duplicate check: user_id + path_title + module_title (case-insensitive)
                    Optional<LearningModuleEntity> existingModuleOpt = moduleRepository
                            .findByTitleAndPathId(moduleDto.title(), path.getId());

                    LearningModuleEntity module;
                    if (existingModuleOpt.isPresent()) {
                        module = existingModuleOpt.get();
                    } else {
                        module = LearningModuleEntity.builder()
                                .path(path)
                                .title(moduleDto.title())
                                .displayOrder(moduleDto.displayOrder() != null ? moduleDto.displayOrder() : 0)
                                .build();
                        module = moduleRepository.save(module);
                        modulesCreated++;
                    }

                    if (moduleDto.topics() == null) continue;

                    for (TopicExportDto topicDto : moduleDto.topics()) {
                        // Topic duplicate check: user_id + module_title + topic_title (case-insensitive)
                        Optional<LearningTopicEntity> existingTopicOpt = topicRepository
                                .findByTitleAndModuleId(topicDto.title(), module.getId());

                        LearningTopicEntity topic;
                        if (existingTopicOpt.isPresent()) {
                            topic = existingTopicOpt.get();
                        } else {
                            topic = LearningTopicEntity.builder()
                                    .module(module)
                                    .title(topicDto.title())
                                    .status(topicDto.status() != null ? topicDto.status() : TopicStatus.NOT_STARTED)
                                    .displayOrder(topicDto.displayOrder() != null ? topicDto.displayOrder() : 0)
                                    .build();
                            topic = topicRepository.save(topic);
                            topicsCreated++;
                        }

                        if (topicDto.resources() == null) continue;

                        // Fetch existing resources under this topic for fast comparison
                        List<LearningResourceEntity> existingResources = resourceRepository.findAllByTopicIdAndUserId(topic.getId(), userId);

                        for (ResourceExportDto resourceDto : topicDto.resources()) {
                            // Resource duplicate check rule implementation
                            boolean isDuplicate = existingResources.stream()
                                    .anyMatch(existing -> isResourceDuplicate(existing.getContent(), resourceDto.content()));

                            if (isDuplicate) {
                                resourcesSkipped++;
                            } else {
                                LearningResourceEntity resource = LearningResourceEntity.builder()
                                        .topic(topic)
                                        .title(resourceDto.title())
                                        .type(resourceDto.type())
                                        .urlDescription(resourceDto.urlDescription() != null ? resourceDto.urlDescription() : "")
                                        .content(resourceDto.content())
                                        .build();

                                resourceRepository.save(resource);
                                existingResources.add(resource); // Add to local list to check against remaining items
                                resourcesCreated++;
                            }
                        }
                    }
                }
            }

            summary = new ImportSummary(pathsCreated, modulesCreated, topicsCreated, resourcesCreated, resourcesSkipped);
            log.info("Import completed for userId: {}. Summary: {}", userId, summary);

            emailService.sendImportSummaryEmail(email, summary);

        } catch (Exception e) {
            emailService.sendImportErrorEmail(email, e);
            throw new ImportExportErrorException(e.getMessage(), e);
        }
    }

    /**
     * Custom Duplicate Logic for Resource Content:
     * If content length > 100, compare first 20 chars AND last 20 chars (case-insensitive).
     * Else compare full content (case-insensitive).
     */
    private boolean isResourceDuplicate(String existing, String incoming) {
        if (existing == null || incoming == null) return false;

        String ext = existing.trim();
        String inc = incoming.trim();

        if (ext.length() > 100 && inc.length() > 100) {
            String extHead = ext.substring(0, 20);
            String extTail = ext.substring(ext.length() - 20);

            String incHead = inc.substring(0, 20);
            String incTail = inc.substring(inc.length() - 20);

            return extHead.equalsIgnoreCase(incHead) && extTail.equalsIgnoreCase(incTail);
        }

        return ext.equalsIgnoreCase(inc);
    }
}
