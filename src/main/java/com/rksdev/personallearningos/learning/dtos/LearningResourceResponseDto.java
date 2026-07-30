package com.rksdev.personallearningos.learning.dtos;

import com.rksdev.personallearningos.learning.model.enums.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningResourceResponseDto {

    @NotNull(message = "ID cannot be null in the response")
    private Long id;

    @NotNull(message = "Topic ID cannot be null")
    private Long topicId;

    @NotNull(message = "Resource type cannot be null")
    private ResourceType type;

    private String urlDescription;

    @NotBlank(message = "Content cannot be blank")
    private String content;

    @NotBlank(message = "Title cannot be null")
    private String title;

    @NotNull(message = "Created at timestamp cannot be null")
    private Instant createdAt;

    @NotNull(message = "Updated at timestamp cannot be null")
    private Instant updatedAt;
}
