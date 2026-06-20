package com.rksdev.personallearningos.learning.dtos;

import com.rksdev.personallearningos.learning.model.enums.PathStatus;
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
public class LearningPathResponseDto {

    @NotNull(message = "ID cannot be null in the response")
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    private String description;

    @NotNull(message = "Status cannot be null")
    private PathStatus status;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Created at timestamp cannot be null")
    private Instant createdAt;

    @NotNull(message = "Updated at timestamp cannot be null")
    private Instant updatedAt;
}