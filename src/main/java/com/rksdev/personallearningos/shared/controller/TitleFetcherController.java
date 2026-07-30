package com.rksdev.personallearningos.shared.controller;

import com.rksdev.personallearningos.shared.service.TitleFetcherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TitleFetcherController {

    private final TitleFetcherService titleFetcherService;

    @GetMapping("/page-title")
    public ResponseEntity<?> getTitle(@RequestParam String url) {
        if (url == null || url.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "url parameter is required"));
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return ResponseEntity.badRequest().body(Map.of("error", "url must start with http:// or https://"));
        }

        try {
            String title = titleFetcherService.fetchTitle(url);
            return ResponseEntity.ok(Map.of("title", title != null ? title : ""));
        } catch (RuntimeException e) {
            return ResponseEntity.status(502).body(Map.of("error", e.getMessage()));
        }
    }
}
