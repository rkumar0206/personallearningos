package com.rksdev.personallearningos.import_export.dtos;

import com.rksdev.personallearningos.learning.model.enums.ResourceType;

public record ResourceExportDto(
        ResourceType type,
        String urlDescription,
        String content,
        String title
) {}
