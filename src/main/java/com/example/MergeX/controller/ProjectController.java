package com.example.MergeX.controller;

import com.example.MergeX.Dto.CreateJoinRequestDto;
import com.example.MergeX.Dto.CreateMessageDto;
import com.example.MergeX.Dto.CreateTaskDto;
import com.example.MergeX.Dto.DashboardSummaryDto;
import com.example.MergeX.Dto.JoinRequestDto;
import com.example.MergeX.Dto.ProjectDtoReq;
import com.example.MergeX.Dto.ProjectDtoRes;
import com.example.MergeX.Dto.ProjectMessageDto;
import com.example.MergeX.Dto.ProjectTaskDto;
import com.example.MergeX.Dto.ProjectWorkspaceDto;
import com.example.MergeX.model.ProjectStatus;
import com.example.MergeX.model.TaskStatus;
import com.example.MergeX.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    @PostMapping("/{projectId}/join")
    public ResponseEntity<String> joinProject(
            @PathVariable Long projectId) {

        projectService.joinProject(projectId);
        return ResponseEntity.ok("Joined project successfully");
    }

    @PostMapping("/{projectId}/request-access")
    public ResponseEntity<String> requestAccess(
            @PathVariable Long projectId,
            @RequestBody(required = false) CreateJoinRequestDto request) {
        projectService.requestToJoin(projectId, request == null ? new CreateJoinRequestDto() : request);
        return ResponseEntity.ok("Access request sent successfully");
    }

    @PostMapping("/{projectId}/invite/{userId}")
    public ResponseEntity<String> inviteUser(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        projectService.inviteToProject(projectId, userId);
        return ResponseEntity.ok("Invitation sent successfully");
    }

    @PostMapping("/requests/{requestId}/approve")
    public ResponseEntity<String> approveRequest(@PathVariable Long requestId) {
        projectService.approveRequest(requestId);
        return ResponseEntity.ok("Join request approved");
    }

    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<String> rejectRequest(@PathVariable Long requestId) {
        projectService.rejectRequest(requestId);
        return ResponseEntity.ok("Join request rejected");
    }

    @PostMapping("/{projectId}/leave")
    public ResponseEntity<String> leaveProject(
            @PathVariable Long projectId) {

        projectService.leaveProject(projectId);
        return ResponseEntity.ok("Left project successfully");
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<String> removeMember(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        projectService.removeMember(projectId, userId);
        return ResponseEntity.ok("Member removed successfully");
    }

    @PostMapping("/create")
    public ResponseEntity<ProjectDtoRes> create(@RequestBody ProjectDtoReq request) {
        return new ResponseEntity<>(projectService.creatProject(request), HttpStatus.CREATED);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDtoRes> update(@PathVariable Long projectId, @RequestBody ProjectDtoReq request) {
        return ResponseEntity.ok(projectService.updateProject(projectId, request));
    }
    @GetMapping("/my-created")
    public ResponseEntity<List<ProjectDtoRes>> getCreatedByOwner() {
        return ResponseEntity.ok(projectService.getProjectsCreated());
    }

    @GetMapping("/my-joined")
    public ResponseEntity<Set<ProjectDtoRes>> getJoinedAsParticipant() {
        return ResponseEntity.ok(projectService.getJoinedProjects());
    }

    // Fetches all projects (Created + Joined)
    @GetMapping("/get-all")
    public ResponseEntity<Set<ProjectDtoRes>> getAllForUser() {
        return ResponseEntity.ok(projectService.getAllUserProjects());
    }
    @GetMapping
    public Page<ProjectDtoRes> exploreProjects(
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) String tech,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {

        String[] sortParams = sort.split(",");
        Sort sortBy = Sort.by(
                Sort.Direction.fromString(sortParams[1]),
                sortParams[0]
        );

        Pageable pageable = PageRequest.of(page, size, sortBy);

        return projectService.exploreProjects(
                keyword,
                pageable);
    }

    @GetMapping("/my-requests")
    public ResponseEntity<List<JoinRequestDto>> getMyRequests() {
        return ResponseEntity.ok(projectService.getMySentRequests());
    }

    @GetMapping("/dashboard-summary")
    public ResponseEntity<DashboardSummaryDto> getDashboardSummary() {
        return ResponseEntity.ok(projectService.getDashboardSummary());
    }

    @GetMapping("/{projectId}/workspace")
    public ResponseEntity<ProjectWorkspaceDto> getWorkspace(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getWorkspace(projectId));
    }

    @PostMapping("/{projectId}/messages")
    public ResponseEntity<ProjectMessageDto> addMessage(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateMessageDto request
    ) {
        return new ResponseEntity<>(projectService.addMessage(projectId, request), HttpStatus.CREATED);
    }

    @PostMapping("/{projectId}/tasks")
    public ResponseEntity<ProjectTaskDto> addTask(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTaskDto request
    ) {
        return new ResponseEntity<>(projectService.addTask(projectId, request), HttpStatus.CREATED);
    }

    @PatchMapping("/tasks/{taskId}/status")
    public ResponseEntity<ProjectTaskDto> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam TaskStatus status
    ) {
        return ResponseEntity.ok(projectService.updateTaskStatus(taskId, status));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/projects/{id}")
    public void deleteProjectByAdmin(@PathVariable Long id) {
        projectService.adminDeleteProject(id);
    }
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/projects/{id}")
    public void deleteOwnProject(@PathVariable Long id) {
        projectService.deleteOwnProject(id);
    }



}
