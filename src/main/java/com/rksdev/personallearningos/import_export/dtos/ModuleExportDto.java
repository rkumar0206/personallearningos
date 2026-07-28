package com.rksdev.personallearningos.import_export.dtos;

import java.util.List;

public record ModuleExportDto(
        String title,
        Integer displayOrder,
        List<TopicExportDto> topics
) {}
