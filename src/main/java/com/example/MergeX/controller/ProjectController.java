package com.example.MergeX.controller;

import com.example.MergeX.Dto.ProjectDtoReq;
import com.example.MergeX.Dto.ProjectDtoRes;
import com.example.MergeX.model.ProjectStatus;
import com.example.MergeX.service.ProjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
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

    @PostMapping("/{projectId}/leave")
    public ResponseEntity<String> leaveProject(
            @PathVariable Long projectId) {

        projectService.leaveProject(projectId);
        return ResponseEntity.ok("Left project successfully");
    }
    @PostMapping("/create")
    public ResponseEntity<ProjectDtoRes> create(@RequestBody ProjectDtoReq request) {
        return new ResponseEntity<>(projectService.creatProject(request), HttpStatus.CREATED);
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
                pageable);
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
