package com.example.MergeX.Dto;

import com.example.MergeX.model.JoinRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JoinRequestDto {
    private Long id;
    private Long requesterId;
    private String requesterName;
    private String requesterEmail;
    private Integer contributionScore;
    private String note;
    private JoinRequestStatus status;
    private LocalDateTime createdAt;
    private String bio;
    private String githubUrl;
    private Set<String> skills;
    private Integer projectsJoinedCount;
    private List<ContributionPointDto> contributionHistory;
    
    // Project info for "My Sent Requests"
    private Long projectId;
    private String projectTitle;
    private Boolean invitedByOwner;
    private String invitedByName;
}
