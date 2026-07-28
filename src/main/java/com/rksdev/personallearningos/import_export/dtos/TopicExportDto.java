package com.rksdev.personallearningos.import_export.dtos;

import com.rksdev.personallearningos.learning.model.enums.TopicStatus;

import java.util.List;

public record TopicExportDto(
        String title,
        TopicStatus status,
        Integer displayOrder,
        List<ResourceExportDto> resources
) {}
