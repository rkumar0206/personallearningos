package com.rksdev.personallearningos.learning.mapper;

import com.rksdev.personallearningos.learning.dtos.LearningResourceRequestDto;
import com.rksdev.personallearningos.learning.dtos.LearningResourceResponseDto;
import com.rksdev.personallearningos.learning.model.LearningResourceEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LearningResourceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "topic.id", source = "topicId")
    LearningResourceEntity toEntity(LearningResourceRequestDto dto);

    @Mapping(target = "topicId", source = "topic.id")
    LearningResourceResponseDto toResponseDto(LearningResourceEntity entity);

    /**
     * Updates an existing entity instance in place from a DTO.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "topic.id", source = "topicId")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void updateEntityFromDto(LearningResourceRequestDto dto, @MappingTarget LearningResourceEntity entity);

    // List Conversion Methods
    List<LearningResourceEntity> toEntityList(List<LearningResourceRequestDto> dtos);

    List<LearningResourceResponseDto> toResponseDtoList(List<LearningResourceEntity> entities);
}
