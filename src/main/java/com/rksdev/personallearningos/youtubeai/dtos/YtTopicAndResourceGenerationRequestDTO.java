package com.rksdev.personallearningos.youtubeai.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YtTopicAndResourceGenerationRequestDTO {

    @NotBlank(message = "Url cannot be blank")
    private String url;

    @NotBlank(message = "Please provide the module or topic title.")
    private String title;
}
