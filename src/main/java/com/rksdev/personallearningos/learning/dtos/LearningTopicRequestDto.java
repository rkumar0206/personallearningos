package com.rksdev.personallearningos.learning.dtos;

import com.rksdev.personallearningos.learning.model.enums.TopicStatus;
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
public class LearningTopicRequestDto {

    @NotNull(message = "Module ID is required")
    private Long moduleId;

    @NotBlank(message = "Title is required and cannot be blank")
    private String title;

    @NotNull(message = "Status is required")
    private TopicStatus status;

    @NotNull(message = "Display order is required")
    private Integer displayOrder;
}