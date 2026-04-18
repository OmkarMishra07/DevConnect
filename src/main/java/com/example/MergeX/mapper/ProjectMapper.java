package com.example.MergeX.mapper;

import com.example.MergeX.Dto.ProjectDtoReq;
import com.example.MergeX.Dto.ProjectDtoRes;
import com.example.MergeX.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Project toEntity(ProjectDtoReq dto);

    @Mapping(target = "createdByName", source = "createdBy.name")
    ProjectDtoRes todto(Project project);
}
