package com.example.MergeX.Dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @Email(message = "Invalid email")
    @NotBlank(message = "Email required")
    private String email;

    @NotBlank(message = "Password required")
    private String password;
}
