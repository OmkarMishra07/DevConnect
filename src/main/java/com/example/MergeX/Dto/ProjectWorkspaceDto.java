package com.example.MergeX.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProjectWorkspaceDto {
    private ProjectDtoRes project;
    private boolean ownerView;
    private boolean memberView;
    private boolean pendingRequest;
    private List<ProjectMessageDto> messages;
    private List<ProjectTaskDto> tasks;
    private List<JoinRequestDto> joinRequests;
}
