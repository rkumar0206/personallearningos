package com.rksdev.personallearningos.import_export.controller;

import com.rksdev.personallearningos.import_export.dtos.UserDataExportDto;
import com.rksdev.personallearningos.import_export.service.UserDataExportService;
import com.rksdev.personallearningos.import_export.service.UserDataImportService;
import com.rksdev.security.web.CurrentUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user-data")
@RequiredArgsConstructor
public class UserDataController {

    private final UserDataExportService exportService;
    private final UserDataImportService importService;
    private final ObjectMapper objectMapper;

    @PostMapping("/export")
    public ResponseEntity<Map<String, String>> triggerExport(
            @CurrentUserId Long userId,
            @RequestParam("email") String email
    ) {
        exportService.exportUserDataAsync(userId, email);

        return ResponseEntity.accepted().body(Map.of(
                "message", "Export process started. You will receive an email with your data shortly."
        ));
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> triggerImport(
            @CurrentUserId Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("email") String email
    ) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Uploaded file is empty."));
        }

        // Fast Sync Phase: Validate JSON Schema/Syntax
        UserDataExportDto importData;
        try {
            importData = objectMapper.readValue(file.getInputStream(), UserDataExportDto.class);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid JSON file structure: " + e.getMessage()));
        }

        // Heavy Async Phase
        importService.importUserDataAsync(userId, importData, email);

        return ResponseEntity.accepted().body(Map.of(
                "message", "Import process started. You will receive an email once complete."
        ));
    }
}
