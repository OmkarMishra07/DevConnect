package com.example.MergeX.controller;

import com.example.MergeX.Dto.CreateMessageDto;
import com.example.MergeX.Dto.ChatGroupDto;
import com.example.MergeX.Dto.FriendshipDto;
import com.example.MergeX.Dto.GroupMessageDto;
import com.example.MergeX.Dto.ParticipantDto;
import com.example.MergeX.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/friend-request/{friendId}")
    public ResponseEntity<String> sendFriendRequest(@PathVariable Long friendId) {
        chatService.sendFriendRequest(friendId);
        return ResponseEntity.ok("Friend request sent");
    }

    @PostMapping("/friend-request/{friendshipId}/accept")
    public ResponseEntity<String> acceptFriendRequest(@PathVariable Long friendshipId) {
        chatService.acceptFriendRequest(friendshipId);
        return ResponseEntity.ok("Friend request accepted");
    }

    @PostMapping("/friend-request/{friendshipId}/reject")
    public ResponseEntity<String> rejectFriendRequest(@PathVariable Long friendshipId) {
        chatService.rejectFriendRequest(friendshipId);
        return ResponseEntity.ok("Friend request rejected");
    }

    @GetMapping("/friends")
    public ResponseEntity<List<ParticipantDto>> getFriends() {
        return ResponseEntity.ok(chatService.getFriends());
    }

    @GetMapping("/friend-requests/pending")
    public ResponseEntity<List<FriendshipDto>> getPendingRequests() {
        return ResponseEntity.ok(chatService.getPendingRequests());
    }

    @PostMapping("/groups")
    public ResponseEntity<ChatGroupDto> createGroup(@RequestParam String name, @RequestParam String description, @RequestBody Set<Long> memberIds) {
        return ResponseEntity.ok(chatService.createGroup(name, description, memberIds));
    }

    @GetMapping("/groups")
    public ResponseEntity<List<ChatGroupDto>> getMyGroups() {
        return ResponseEntity.ok(chatService.getMyGroups());
    }

    @PostMapping("/groups/{groupId}/messages")
    public ResponseEntity<GroupMessageDto> sendMessage(@PathVariable Long groupId, @RequestBody CreateMessageDto request) {
        return ResponseEntity.ok(chatService.sendMessage(groupId, request.getContent()));
    }

    @GetMapping("/groups/{groupId}/messages")
    public ResponseEntity<List<GroupMessageDto>> getGroupMessages(@PathVariable Long groupId) {
        return ResponseEntity.ok(chatService.getGroupMessages(groupId));
    }
}
