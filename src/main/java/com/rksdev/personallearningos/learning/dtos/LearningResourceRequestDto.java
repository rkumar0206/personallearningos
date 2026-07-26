package com.rksdev.personallearningos.learning.dtos;

import com.rksdev.personallearningos.learning.model.enums.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningResourceRequestDto {

    @NotNull(message = "Topic ID is required")
    private Long topicId;

    @NotNull(message = "Resource type is required")
    private ResourceType type;

    private String urlDescription;

    @NotBlank(message = "Content is required and cannot be blank")
    private String content;
}