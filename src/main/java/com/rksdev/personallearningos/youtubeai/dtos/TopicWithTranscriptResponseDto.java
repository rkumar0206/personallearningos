package com.rksdev.personallearningos.youtubeai.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TopicWithTranscriptResponseDto {

    private List<TopicAiResponseDto> topics;

    private String transcript;

    @JsonProperty("raw_transcript")
    private List<Map<String, Object>> rawTranscript;
}
