package com.example.MergeX.repository;

import com.example.MergeX.model.ProjectTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {
    List<ProjectTask> findByProjectIdOrderByCreatedAtDesc(Long projectId);
}
