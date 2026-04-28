package com.example.MergeX.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMessageDto {
    @NotBlank
    @Size(max = 600)
    private String content;
}
