package com.example.MergeX.Dto;

import com.example.MergeX.model.College;
import com.example.MergeX.model.Education;
import lombok.*;
import java.util.Set;
@Getter
@Setter

public class UserDtoRes{

    private Long id;
    private String name;
    private String email;
    private String githubUrl;
    private Education education;
    private String collegeName;
    private Set<String> skills;
    private String bio;
}