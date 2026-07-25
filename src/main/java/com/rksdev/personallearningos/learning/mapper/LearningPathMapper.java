package com.rksdev.personallearningos.learning.mapper;

import com.rksdev.personallearningos.learning.dtos.LearningPathRequestDto;
import com.rksdev.personallearningos.learning.dtos.LearningPathResponseDto;
import com.rksdev.personallearningos.learning.model.LearningPathEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LearningPathMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "modules", ignore = true)
    @Mapping(target = "user", ignore = true)
    LearningPathEntity toEntity(LearningPathRequestDto dto);

    @Mapping(target = "userId", source = "user.id")
    LearningPathResponseDto toResponseDto(LearningPathEntity entity);

    // List Conversion Methods
    List<LearningPathEntity> toEntityList(List<LearningPathRequestDto> dtos);

    List<LearningPathResponseDto> toResponseDtoList(List<LearningPathEntity> entities);
}
