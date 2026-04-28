package com.example.MergeX.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDtoRes {

        private Long id;
        private String title;
        private String description;
        private String tagline;
        private Set<String> techStack;
        private String createdByName;
        private LocalDateTime createdAt;
        private Set<ParticipantDto> joinedUsers;
        private int participantCount;
        private long matchScore;
        private Integer maxTeamSize;

}
