package com.example.MergeX.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class CreateTaskDto {
    @NotBlank
    @Size(max = 140)
    private String title;

    @Size(max = 500)
    private String description;

    private Long assignedToId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    private Set<String> tags;
}
