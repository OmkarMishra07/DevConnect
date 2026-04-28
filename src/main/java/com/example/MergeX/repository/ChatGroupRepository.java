package com.example.MergeX.repository;

import com.example.MergeX.model.ChatGroup;
import com.example.MergeX.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {
    List<ChatGroup> findByMembersContaining(User user);
}
