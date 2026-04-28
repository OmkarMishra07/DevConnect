package com.example.MergeX.Dto;

import com.example.MergeX.model.Education;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter
@AllArgsConstructor
public class UserProfileResponseDto {

    private Long id;
    private String name;
    private String email;
    private Education education;
    private String collegeName;
    private String domain;
    private Set<String> skills;
    private String githubUrl;
    private String bio;
    private Integer contributionScore;
    private boolean profileCompleted;
}
