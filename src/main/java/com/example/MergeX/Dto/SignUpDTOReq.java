package com.example.MergeX.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpDTOReq {
    @NotBlank(message = "Name is required")
    private String name;
    @Email(message = "Invalid email")
    @NotBlank(message = "Email required")
    private String email;
    @NotBlank(message = "Password cannot be blank")
    private String password;

}
