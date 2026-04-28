package com.example.MergeX.repository;

import com.example.MergeX.model.ChatGroup;
import com.example.MergeX.model.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {
    List<GroupMessage> findByGroupOrderByCreatedAtAsc(ChatGroup group);
}
