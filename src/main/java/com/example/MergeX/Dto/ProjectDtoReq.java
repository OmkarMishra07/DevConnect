package com.example.MergeX.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ProjectDtoReq {
    private String title;
    private String description;
    private String tagline;
    private Set<String> techStack;
    private Integer maxTeamSize;
}
