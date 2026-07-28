package com.rksdev.personallearningos.import_export.dtos;

import com.rksdev.personallearningos.learning.model.enums.PathStatus;

import java.util.List;

public record PathExportDto(
        String title,
        String description,
        PathStatus status,
        List<ModuleExportDto> modules
) {}
