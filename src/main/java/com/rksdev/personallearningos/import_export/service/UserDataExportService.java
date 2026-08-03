package com.rksdev.personallearningos.import_export.service;

import com.rksdev.personallearningos.import_export.dtos.*;
import com.rksdev.personallearningos.learning.model.LearningPathEntity;
import com.rksdev.personallearningos.learning.repository.LearningPathRepository;
import com.rksdev.personallearningos.shared.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDataExportService {

    private final LearningPathRepository pathRepository;
    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @Async
    @Transactional(readOnly = true)
    public void exportUserDataAsync(Long userId, String userEmail) {
        log.info("Starting async data export for userId: {}", userId);
        try {
            List<LearningPathEntity> paths = pathRepository.findAllByUserId(userId);

            List<PathExportDto> pathDtos = paths.stream().map(path ->
                    new PathExportDto(
                            path.getTitle(),
                            path.getDescription(),
                            path.getStatus(),
                            path.getModules().stream().map(module ->
                                    new ModuleExportDto(
                                            module.getTitle(),
                                            module.getDisplayOrder(),
                                            module.getTopics().stream().map(topic ->
                                                    new TopicExportDto(
                                                            topic.getTitle(),
                                                            topic.getStatus(),
                                                            topic.getDisplayOrder(),
                                                            topic.getResources().stream().map(resource ->
                                                                    new ResourceExportDto(
                                                                            resource.getType(),
                                                                            resource.getUrlDescription(),
                                                                            resource.getContent(),
                                                                            resource.getTitle()
                                                                    )
                                                            ).toList()
                                                    )
                                            ).toList()
                                    )
                            ).toList()
                    )
            ).toList();

            UserDataExportDto exportDto = new UserDataExportDto(Instant.now(), pathDtos);
            byte[] jsonBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(exportDto);

            emailService.sendDataExportEmail(userEmail, jsonBytes);
            log.info("Data export successfully sent to email: {}", userEmail);

        } catch (Exception e) {
            log.error("Error occurred while exporting data for userId: {}", userId, e);
        }
    }
}
