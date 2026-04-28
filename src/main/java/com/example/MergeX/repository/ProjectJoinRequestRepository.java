package com.example.MergeX.repository;

import com.example.MergeX.model.JoinRequestStatus;
import com.example.MergeX.model.Project;
import com.example.MergeX.model.ProjectJoinRequest;
import com.example.MergeX.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectJoinRequestRepository extends JpaRepository<ProjectJoinRequest, Long> {
    Optional<ProjectJoinRequest> findByProjectAndRequesterAndStatus(Project project, User requester, JoinRequestStatus status);

    List<ProjectJoinRequest> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    List<ProjectJoinRequest> findByRequesterOrderByCreatedAtDesc(User requester);

    boolean existsByProjectAndRequester(Project project, User requester);
}
