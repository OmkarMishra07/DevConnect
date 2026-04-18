package com.example.MergeX.Dto;

import com.example.MergeX.model.Education;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserDtoReq {

        @NotBlank(message = "Name is required")
        private String name;

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        private String githubUrl;

        @NotNull(message = "Education is required")
        private Education education;

        private Set<String> skills;

        @Size(max = 300, message = "Bio too long")
        private String bio;

        @NotNull(message = "College is required")
        private Long collegeId;
}
