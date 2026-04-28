package com.example.MergeX.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DashboardSummaryDto {
    private UserProfileResponseDto profile;
    private int activeProjects;
    private int ownedProjects;
    private int pendingApprovals;
    private int tasksDue;
    private List<ProjectDtoRes> projects;
    private List<ProjectTaskDto> tasks;
    private List<ContributionPointDto> contributionHistory;
}
