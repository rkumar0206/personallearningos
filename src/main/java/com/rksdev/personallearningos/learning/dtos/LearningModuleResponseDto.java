package com.rksdev.personallearningos.learning.dtos;

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
public class LearningModuleResponseDto {

    @NotNull(message = "ID cannot be null in the response")
    private Long id;

    @NotNull(message = "Path ID cannot be null")
    private Long pathId;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotNull(message = "Display order cannot be null")
    private Integer displayOrder;

    @NotNull(message = "Created at timestamp cannot be null")
    private Instant createdAt;

    @NotNull(message = "Updated at timestamp cannot be null")
    private Instant updatedAt;
}
