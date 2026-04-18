package com.example.MergeX.service;

import com.example.MergeX.Dto.ProjectDtoReq;
import com.example.MergeX.Dto.ProjectDtoRes;
import com.example.MergeX.Security.AuthUtil;
import com.example.MergeX.Security.UserProfileUtil;
import com.example.MergeX.exceptions.ResourceNotFound;
import com.example.MergeX.mapper.ProjectMapper;
import com.example.MergeX.model.Project;
import com.example.MergeX.model.ProjectStatus;
import com.example.MergeX.model.User;
import com.example.MergeX.repository.ProjectRepository;
import com.example.MergeX.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ModelMapper modelMapper;

    public ProjectDtoRes creatProject(ProjectDtoReq request){
        String email = AuthUtil.getCurrentUserEmail();
        User creator = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        if (!UserProfileUtil.isProfileComplete(creator)) {
            throw new RuntimeException(
                    "Please complete your profile before creating a project"
            );
        }
        Project project = projectMapper.toEntity(request);
        project.setCreatedBy(creator);
        project.getParticipants().add(creator);
        return projectMapper.todto(projectRepository.save(project));
    }
    public void joinProject(Long projectId) {

        String email = AuthUtil.getCurrentUserEmail();
        System.out.println("EMAIL FROM TOKEN: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFound("Project not found"));

        // prevent duplicate join
        if (project.getParticipants().contains(user)) {
            throw new IllegalStateException("You have already joined this project");

        }

        project.getParticipants().add(user);
        projectRepository.save(project);
    }

    public void leaveProject(Long projectId) {

        String email = AuthUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFound("Project not found"));

        // creator cannot leave his own project
        if (project.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Project owner cannot leave");
        }

        project.getParticipants().remove(user);
        projectRepository.save(project);
    }
    public List<ProjectDtoRes> getProjectsCreated(){
        String email = AuthUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

         return projectRepository.findByCreatedBy(user).stream().map((element) -> modelMapper.map(element, ProjectDtoRes.class)).toList();
    }
    public Set<ProjectDtoRes> getJoinedProjects(){
        String email = AuthUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Set<Project> projects = projectRepository.findAllByParticipantsContaining(user);
        return projects.stream().map(projectMapper::todto).collect(Collectors.toSet());
    }

    public Set<ProjectDtoRes> getAllUserProjects() {
        String email = AuthUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Project> allProjects = new HashSet<>();
        allProjects.addAll(projectRepository.findAllByCreatedBy(user));
        allProjects.addAll(projectRepository.findAllByParticipantsContaining(user));

        return allProjects.stream()
                .map(projectMapper::todto)
                .collect(Collectors.toSet());
    }
    public Page<ProjectDtoRes> exploreProjects(Pageable pageable) {

        String email = AuthUtil.getCurrentUserEmail();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        Page<Project> page = projectRepository.findExploreProjects(
                currentUser.getId(),
                currentUser,
                pageable
        );

        return page.map(projectMapper::todto)

                ;

    }




    public void deleteOwnProject(Long projectId) {

        String email = AuthUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFound("User not found"));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFound("Project not found"));

        if (!project.getCreatedBy().getId().equals(user.getId())) {
            throw new SecurityException("Not allowed to delete this project");
        }

        projectRepository.delete(project);
    }


    public void adminDeleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFound("Project not found"));

        projectRepository.delete(project);
    }
}
