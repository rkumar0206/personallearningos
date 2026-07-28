package com.rksdev.personallearningos.import_export.dtos;

import java.time.Instant;
import java.util.List;

public record UserDataExportDto(
        Instant exportedAt,
        List<PathExportDto> paths
) {
}

