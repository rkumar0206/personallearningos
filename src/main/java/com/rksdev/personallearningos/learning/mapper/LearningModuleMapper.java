package com.rksdev.personallearningos.learning.mapper;

import com.rksdev.personallearningos.learning.dtos.LearningModuleRequestDto;
import com.rksdev.personallearningos.learning.dtos.LearningModuleResponseDto;
import com.rksdev.personallearningos.learning.model.LearningModuleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LearningModuleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "path.id", source = "pathId")
    @Mapping(target = "topics", ignore = true)
    LearningModuleEntity toEntity(LearningModuleRequestDto dto);

    @Mapping(target = "pathId", source = "path.id")
    LearningModuleResponseDto toResponseDto(LearningModuleEntity entity);

    // List Conversion Methods
    List<LearningModuleEntity> toEntityList(List<LearningModuleRequestDto> dtos);

    List<LearningModuleResponseDto> toResponseDtoList(List<LearningModuleEntity> entities);
}