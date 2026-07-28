package com.rksdev.personallearningos.import_export.dtos;

public record ImportSummary(
        int pathsCreated,
        int modulesCreated,
        int topicsCreated,
        int resourcesCreated,
        int resourcesSkipped
) {}
