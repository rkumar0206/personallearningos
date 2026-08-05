package com.rksdev.personallearningos.youtubeai.controller;


import com.rksdev.personallearningos.youtubeai.dtos.LearningResourceWithTranscriptResponseDto;
import com.rksdev.personallearningos.youtubeai.dtos.TopicWithTranscriptResponseDto;
import com.rksdev.personallearningos.youtubeai.dtos.YtTopicAndResourceGenerationRequestDTO;
import com.rksdev.personallearningos.youtubeai.service.YoutubeAiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai/yt/generate")
@RequiredArgsConstructor
public class YoutubeAiController {

    private final YoutubeAiService youtubeAiService;

    @PostMapping("/for-module")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TopicWithTranscriptResponseDto> generateModuleContent(
            @Valid @RequestBody YtTopicAndResourceGenerationRequestDTO request,
            @RequestParam(name = "include_raw_transcript", defaultValue = "false") boolean includeRawTranscript
    ) {
        TopicWithTranscriptResponseDto response = youtubeAiService.generateModuleContent(request, includeRawTranscript);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/for-topic")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LearningResourceWithTranscriptResponseDto> generateTopicContent(
            @Valid @RequestBody YtTopicAndResourceGenerationRequestDTO request,
            @RequestParam(name = "include_raw_transcript", defaultValue = "false") boolean includeRawTranscript
    ) {
        LearningResourceWithTranscriptResponseDto response = youtubeAiService.generateTopicContent(request, includeRawTranscript);
        return ResponseEntity.ok(response);
    }
}