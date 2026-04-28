package com.example.MergeX.service;

import com.example.MergeX.Dto.ChatGroupDto;
import com.example.MergeX.Dto.FriendshipDto;
import com.example.MergeX.Dto.GroupMessageDto;
import com.example.MergeX.Dto.ParticipantDto;
import com.example.MergeX.Security.AuthUtil;
import com.example.MergeX.exceptions.ResourceNotFound;
import com.example.MergeX.model.*;
import com.example.MergeX.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final ChatGroupRepository chatGroupRepository;
    private final GroupMessageRepository groupMessageRepository;
    private final ModelMapper modelMapper;

    public void sendFriendRequest(Long friendId) {
        User user = getCurrentUser();
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        if (friendshipRepository.findByUserAndFriend(user, friend).isPresent()) {
            throw new IllegalStateException("Friend request already sent");
        }

        Friendship friendship = new Friendship();
        friendship.setUser(user);
        friendship.setFriend(friend);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendshipRepository.save(friendship);
    }

    public void acceptFriendRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new ResourceNotFound("Friendship not found"));
        
        User user = getCurrentUser();
        if (!friendship.getFriend().getId().equals(user.getId())) {
            throw new SecurityException("Only the recipient can accept friend requests");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);

        // Create reciprocal friendship
        Friendship reciprocal = new Friendship();
        reciprocal.setUser(friendship.getFriend());
        reciprocal.setFriend(friendship.getUser());
        reciprocal.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(reciprocal);
    }

    public void rejectFriendRequest(Long friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new ResourceNotFound("Friendship not found"));

        User user = getCurrentUser();
        if (!friendship.getFriend().getId().equals(user.getId())) {
            throw new SecurityException("Only the recipient can reject friend requests");
        }

        friendshipRepository.delete(friendship);
    }

    public List<ParticipantDto> getFriends() {
        User user = getCurrentUser();
        return friendshipRepository.findByUserAndStatus(user, FriendshipStatus.ACCEPTED).stream()
                .map(f -> modelMapper.map(f.getFriend(), ParticipantDto.class))
                .collect(Collectors.toList());
    }

    public List<FriendshipDto> getPendingRequests() {
        User user = getCurrentUser();
        return friendshipRepository.findByFriendAndStatus(user, FriendshipStatus.PENDING).stream()
                .map(this::toFriendshipDto)
                .collect(Collectors.toList());
    }

    public ChatGroupDto createGroup(String name, String description, Set<Long> memberIds) {
        User user = getCurrentUser();
        ChatGroup group = new ChatGroup();
        group.setName(name);
        group.setDescription(description);
        group.setCreatedBy(user);
        group.getMembers().add(user);

        for (Long memberId : memberIds) {
            User member = userRepository.findById(memberId)
                    .orElseThrow(() -> new ResourceNotFound("User not found"));
            group.getMembers().add(member);
        }

        return toChatGroupDto(chatGroupRepository.save(group));
    }

    public GroupMessageDto sendMessage(Long groupId, String content) {
        User user = getCurrentUser();
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFound("Group not found"));

        if (!group.getMembers().contains(user)) {
            throw new SecurityException("You are not a member of this group");
        }

        GroupMessage message = new GroupMessage();
        message.setGroup(group);
        message.setSender(user);
        message.setContent(content.trim());
        return toGroupMessageDto(groupMessageRepository.save(message));
    }

    public List<ChatGroupDto> getMyGroups() {
        User user = getCurrentUser();
        return chatGroupRepository.findByMembersContaining(user).stream()
                .map(this::toChatGroupDto)
                .collect(Collectors.toList());
    }

    public List<GroupMessageDto> getGroupMessages(Long groupId) {
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFound("Group not found"));
        return groupMessageRepository.findByGroupOrderByCreatedAtAsc(group).stream()
                .map(this::toGroupMessageDto)
                .collect(Collectors.toList());
    }

    private User getCurrentUser() {
        String email = AuthUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFound("User not found"));
    }

    private ChatGroupDto toChatGroupDto(ChatGroup group) {
        return new ChatGroupDto(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getMembers().stream()
                        .map(m -> modelMapper.map(m, ParticipantDto.class))
                        .collect(Collectors.toSet())
        );
    }

    private GroupMessageDto toGroupMessageDto(GroupMessage message) {
        return new GroupMessageDto(
                message.getId(),
                message.getGroup().getId(),
                message.getSender().getId(),
                message.getSender().getName(),
                message.getContent(),
                message.getCreatedAt()
        );
    }

    private FriendshipDto toFriendshipDto(Friendship friendship) {
        return new FriendshipDto(
                friendship.getId(),
                modelMapper.map(friendship.getUser(), ParticipantDto.class),
                modelMapper.map(friendship.getFriend(), ParticipantDto.class),
                friendship.getStatus()
        );
    }
}
