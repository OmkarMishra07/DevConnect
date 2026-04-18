package com.example.MergeX.Dto;

import com.example.MergeX.model.Education;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CompleteProfileRequestDto {
    private Education education;
    private String collegeName;
    private Set<String> skills;
    private String githubUrl;
    private String bio;
}
