package com.example.MergeX.mapper;

import com.example.MergeX.Dto.ParticipantDto;
import com.example.MergeX.Dto.ProjectDtoReq;
import com.example.MergeX.Dto.ProjectDtoRes;
import com.example.MergeX.model.Project;
import com.example.MergeX.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "participants", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "maxTeamSize", source = "maxTeamSize")
    Project toEntity(ProjectDtoReq dto);

    @Mapping(target = "createdByName", source = "createdBy.name")
    @Mapping(target = "joinedUsers", source = "participants")
    @Mapping(target = "participantCount", expression = "java(project.getParticipants() == null ? 0 : project.getParticipants().size())")
    @Mapping(target = "matchScore", ignore = true)
    ProjectDtoRes todto(Project project);

    ParticipantDto userToParticipantDto(User user);
}
