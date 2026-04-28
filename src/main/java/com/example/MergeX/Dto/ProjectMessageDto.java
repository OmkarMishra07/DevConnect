package com.example.MergeX.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ProjectMessageDto {
    private Long id;
    private Long senderId;
    private String senderName;
    private Integer senderScore;
    private String content;
    private LocalDateTime createdAt;
}
