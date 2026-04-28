package com.example.MergeX.repository;

import com.example.MergeX.model.ProjectMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMessageRepository extends JpaRepository<ProjectMessage, Long> {
    List<ProjectMessage> findByProjectIdOrderByCreatedAtAsc(Long projectId);
}
