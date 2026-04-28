package com.example.MergeX.Dto;

import com.example.MergeX.model.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipDto {
    private Long id;
    private ParticipantDto user; // requester
    private ParticipantDto friend; // receiver
    private FriendshipStatus status;
}
