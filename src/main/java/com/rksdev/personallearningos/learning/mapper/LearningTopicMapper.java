package com.rksdev.personallearningos.learning.mapper;

import com.rksdev.personallearningos.learning.dtos.LearningTopicRequestDto;
import com.rksdev.personallearningos.learning.dtos.LearningTopicResponseDto;
import com.rksdev.personallearningos.learning.model.LearningTopicEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LearningTopicMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "module.id", source = "moduleId")
    @Mapping(target = "resources", ignore = true)
    LearningTopicEntity toEntity(LearningTopicRequestDto dto);

    @Mapping(target = "moduleId", source = "module.id")
    LearningTopicResponseDto toResponseDto(LearningTopicEntity entity);

    // List Conversion Methods
    List<LearningTopicEntity> toEntityList(List<LearningTopicRequestDto> dtos);

    List<LearningTopicResponseDto> toResponseDtoList(List<LearningTopicEntity> entities);
}
