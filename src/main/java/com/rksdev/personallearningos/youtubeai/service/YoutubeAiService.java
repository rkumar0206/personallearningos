package com.rksdev.personallearningos.youtubeai.service;

import com.rksdev.personallearningos.youtubeai.dtos.LearningResourceWithTranscriptResponseDto;
import com.rksdev.personallearningos.youtubeai.dtos.TopicWithTranscriptResponseDto;
import com.rksdev.personallearningos.youtubeai.dtos.YtTopicAndResourceGenerationRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class YoutubeAiService {

    private final RestClient restClient;
    @Value("${ai-service.youtube.transcriptProvider}")
    private String  transcriptProvider;

    public YoutubeAiService(@Value("${ai-service.youtube.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public TopicWithTranscriptResponseDto generateModuleContent(
            YtTopicAndResourceGenerationRequestDTO request,
            boolean includeRawTranscript
    ) {
        return restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/generate/module")
                        .queryParam("include_raw_transcript", includeRawTranscript)
                        .queryParam("provider", transcriptProvider)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(TopicWithTranscriptResponseDto.class);
    }

    public LearningResourceWithTranscriptResponseDto generateTopicContent(
            YtTopicAndResourceGenerationRequestDTO request,
            boolean includeRawTranscript
    ) {
        return restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/generate/topic")
                        .queryParam("include_raw_transcript", includeRawTranscript)
                        .queryParam("provider", transcriptProvider)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(LearningResourceWithTranscriptResponseDto.class);
    }
}
