package com.rksdev.personallearningos.youtubeai.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningResourceAiResponseDto {
    private String type; // LINK
    private String content; // https://youtu.be/{id}?t={sec}
    private String title;
}
