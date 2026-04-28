package com.example.MergeX.Dto;

import com.example.MergeX.model.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectTaskDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Long createdById;
    private String createdByName;
    private Long assignedToId;
    private String assignedToName;
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;
    
    private Long projectId;
    private String projectTitle;
    private Set<String> tags;
}
