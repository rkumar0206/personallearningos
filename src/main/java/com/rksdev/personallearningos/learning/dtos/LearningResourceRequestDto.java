package com.rksdev.personallearningos.learning.dtos;

import com.rksdev.personallearningos.learning.model.enums.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Title is required and cannot be blank")
    @Size(max = 200, min = 3, message = "Title length should be more than 2 and less than 200")
    private String title;
}