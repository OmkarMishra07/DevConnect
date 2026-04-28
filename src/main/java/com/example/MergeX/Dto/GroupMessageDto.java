package com.example.MergeX.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupMessageDto {
    private Long id;
    private Long groupId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime createdAt;
}
