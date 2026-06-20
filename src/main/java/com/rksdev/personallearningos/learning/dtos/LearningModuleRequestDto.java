package com.rksdev.personallearningos.learning.dtos;

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
public class LearningModuleRequestDto {

    @NotNull(message = "Path ID is required")
    private Long pathId;

    @NotBlank(message = "Title is required and cannot be blank")
    private String title;

    @NotNull(message = "Display order is required")
    private Integer displayOrder;
}
