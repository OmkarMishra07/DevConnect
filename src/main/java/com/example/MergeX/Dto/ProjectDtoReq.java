package com.example.MergeX.Dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ProjectDtoReq {

    @NotBlank(message = "Project title required")
    @Size(min = 3, max = 100)
    private String title;

    @Size(max = 1000)
    private String description;

    @NotEmpty(message = "Tech stack cannot be empty")
    private Set<String> techStack;
}
