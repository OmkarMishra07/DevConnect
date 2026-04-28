package com.example.MergeX.service;

import com.example.MergeX.Dto.ContributionPointDto;
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
import com.example.MergeX.Dto.UserProfileResponseDto;
import com.example.MergeX.Security.AuthUtil;
import com.example.MergeX.Security.UserProfileUtil;
import com.example.MergeX.exceptions.ResourceNotFound;
import com.example.MergeX.mapper.ProjectMapper;
import com.example.MergeX.model.JoinRequestStatus;
import com.example.MergeX.model.Project;
import com.example.MergeX.model.ProjectJoinRequest;
import com.example.MergeX.model.ProjectMessage;
import com.example.MergeX.model.ProjectTask;
import com.example.MergeX.model.User;
import com.example.MergeX.model.TaskStatus;
import com.example.MergeX.repository.ProjectJoinRequestRepository;
import com.example.MergeX.repository.ProjectMessageRepository;
import com.example.MergeX.repository.ProjectRepository;
import com.example.MergeX.repository.ProjectTaskRepository;
import com.example.MergeX.repository.UserRepository;
import com.example.MergeX.repository.ContributionHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectJoinRequestRepository projectJoinRequestRepository;
    private final ProjectMessageRepository projectMessageRepository;
    private final ProjectTaskRepository projectTaskRepository;
    private final ProjectMapper projectMapper;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final ContributionHistoryRepository contributionHistoryRepository;

    public ProjectDtoRes creatProject(ProjectDtoReq request){
        User creator = getCurrentUser();
        if (!UserProfileUtil.isProfileComplete(creator)) {
            throw new RuntimeException(
                    "Please complete your profile before creating a project"
            );
        }
        Project project = projectMapper.toEntity(request);
        project.setCreatedBy(creator);
        project.getParticipants().add(creator);
        project.setMaxTeamSize(request.getMaxTeamSize() != null ? request.getMaxTeamSize() : 10);
        return projectMapper.todto(projectRepository.save(project));
    }

    public void joinProject(Long projectId) {
        requestToJoin(projectId, new CreateJoinRequestDto());
    }

    public void requestToJoin(Long projectId, CreateJoinRequestDto request) {
        User user = getCurrentUser();
        Project project = getProject(projectId);

        if (project.getCreatedBy().getId().equals(user.getId())) {
            throw new IllegalStateException("Project owners are already part of the workspace");
        }
        if (project.getParticipants().contains(user)) {
            throw new IllegalStateException("You are already part of this project");
        }
        if (project.getMaxTeamSize() != null && project.getParticipants().size() >= project.getMaxTeamSize()) {
            throw new IllegalStateException("Project team size limit reached");
        }
        if (projectJoinRequestRepository.findByProjectAndRequesterAndStatus(project, user, JoinRequestStatus.PENDING).isPresent()) {
            throw new IllegalStateException("You already have a pending request for this project");
        }

        ProjectJoinRequest joinRequest = new ProjectJoinRequest();
        joinRequest.setProject(project);
        joinRequest.setRequester(user);
        joinRequest.setStatus(JoinRequestStatus.PENDING);
        joinRequest.setNote(request.getNote());
        projectJoinRequestRepository.save(joinRequest);
    }

    public ProjectDtoRes updateProject(Long projectId, ProjectDtoReq request) {
        User currentUser = getCurrentUser();
        Project project = getProject(projectId);
        
        if (!project.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new SecurityException("Only owner can update project settings");
        }
        
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setTagline(request.getTagline());
        project.setTechStack(request.getTechStack());
        project.setMaxTeamSize(request.getMaxTeamSize());
        
        return projectMapper.todto(projectRepository.save(project));
    }

    @Transactional
    public void approveRequest(Long requestId) {
        ProjectJoinRequest request = getJoinRequest(requestId);
        Project project = request.getProject();
        User currentUser = getCurrentUser();

        boolean isOwner = project.getCreatedBy().getId().equals(currentUser.getId());
        boolean isInvitedUser = Boolean.TRUE.equals(request.getInvitedByOwner()) && request.getRequester().getId().equals(currentUser.getId());

        if (!isOwner && !isInvitedUser) {
            throw new SecurityException("Not authorized to approve this request");
        }

        if (request.getStatus() != JoinRequestStatus.PENDING) {
            throw new IllegalStateException("Request has already been reviewed");
        }

        request.setStatus(JoinRequestStatus.APPROVED);
        request.setReviewedAt(LocalDateTime.now());
        project.getParticipants().add(request.getRequester());
        request.getRequester().setContributionScore(Math.min(100, request.getRequester().getContributionScore() + 3));
        userService.logContributionHistory(request.getRequester());
        projectRepository.save(project);
        projectJoinRequestRepository.save(request);
    }

    @Transactional
    public void rejectRequest(Long requestId) {
        ProjectJoinRequest request = getJoinRequest(requestId);
        User currentUser = getCurrentUser();

        boolean isOwner = request.getProject().getCreatedBy().getId().equals(currentUser.getId());
        boolean isInvitedUser = Boolean.TRUE.equals(request.getInvitedByOwner()) && request.getRequester().getId().equals(currentUser.getId());

        if (!isOwner && !isInvitedUser) {
            throw new SecurityException("Not authorized to reject this request");
        }

        if (request.getStatus() != JoinRequestStatus.PENDING) {
            throw new IllegalStateException("Request has already been reviewed");
        }

        request.setStatus(JoinRequestStatus.REJECTED);
        request.setReviewedAt(LocalDateTime.now());
        projectJoinRequestRepository.save(request);
    }

    public void leaveProject(Long projectId) {
        User user = getCurrentUser();
        Project project = getProject(projectId);

        // creator cannot leave his own project
        if (project.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Project owner cannot leave");
        }

        project.getParticipants().remove(user);
        projectRepository.save(project);
    }

    public void removeMember(Long projectId, Long userId) {
        User currentUser = getCurrentUser();
        Project project = getProject(projectId);

        if (!project.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new SecurityException("Only the project owner can remove members");
        }

        User memberToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("User not found"));

        if (project.getCreatedBy().getId().equals(userId)) {
            throw new IllegalStateException("Project owner cannot be removed");
        }

        project.getParticipants().remove(memberToRemove);
        projectRepository.save(project);
    }

    @Transactional
    public void inviteToProject(Long projectId, Long userId) {
        String email = AuthUtil.getCurrentUserEmail();
        User owner = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFound("User not found"));
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new ResourceNotFound("Project not found"));

        if (!project.getCreatedBy().getId().equals(owner.getId())) {
            throw new SecurityException("Only the owner can invite users to this project.");
        }

        User invitee = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFound("Invited user not found"));

        if (project.getParticipants().contains(invitee) || project.getCreatedBy().getId().equals(invitee.getId())) {
            throw new IllegalStateException("User is already a member of this project.");
        }

        if (projectJoinRequestRepository.existsByProjectAndRequester(project, invitee)) {
            throw new IllegalStateException("An active join request or invitation already exists for this user.");
        }

        ProjectJoinRequest request = new ProjectJoinRequest();
        request.setProject(project);
        request.setRequester(invitee);
        request.setInvitedByOwner(true);
        request.setStatus(JoinRequestStatus.PENDING);
        request.setNote("Invited by owner: " + owner.getName());
        projectJoinRequestRepository.save(request);
    }

    public List<ProjectDtoRes> getProjectsCreated() {
        User user = getCurrentUser();
         return projectRepository.findByCreatedBy(user).stream().map((element) -> modelMapper.map(element, ProjectDtoRes.class)).toList();
    }
    public Set<ProjectDtoRes> getJoinedProjects(){
        User user = getCurrentUser();
        Set<Project> projects = projectRepository.findAllByParticipantsContaining(user);
        return projects.stream().map(projectMapper::todto).collect(Collectors.toSet());
    }

    public Set<ProjectDtoRes> getAllUserProjects() {
        User user = getCurrentUser();

        Set<Project> allProjects = new HashSet<>();
        allProjects.addAll(projectRepository.findAllByCreatedBy(user));
        allProjects.addAll(projectRepository.findAllByParticipantsContaining(user));

        return allProjects.stream()
                .map(projectMapper::todto)
                .collect(Collectors.toSet());
    }
    public Page<ProjectDtoRes> exploreProjects(String keyword, Pageable pageable) {
        User currentUser = getCurrentUser();

        Page<Project> page = projectRepository.findExploreProjects(
                currentUser.getId(),
                currentUser,
                keyword,
                pageable
        );

        List<ProjectDtoRes> rankedProjects = page.getContent().stream()
                .map(projectMapper::todto)
                .peek(project -> project.setMatchScore(calculateMatchScore(project, currentUser)))
                .sorted(Comparator.comparingLong(ProjectDtoRes::getMatchScore).reversed())
                .toList();

        return new PageImpl<>(rankedProjects, pageable, page.getTotalElements());

    }

    public List<JoinRequestDto> getMySentRequests() {
        User currentUser = getCurrentUser();
        return projectJoinRequestRepository.findByRequesterOrderByCreatedAtDesc(currentUser).stream()
                .map(this::toJoinRequestDto)
                .toList();
    }

    public ProjectWorkspaceDto getWorkspace(Long projectId) {
        User currentUser = getCurrentUser();
        Project project = getProject(projectId);
        boolean ownerView = project.getCreatedBy().getId().equals(currentUser.getId());
        boolean memberView = ownerView || project.getParticipants().stream().anyMatch(user -> user.getId().equals(currentUser.getId()));
        boolean pendingRequest = projectJoinRequestRepository
                .findByProjectAndRequesterAndStatus(project, currentUser, JoinRequestStatus.PENDING)
                .isPresent();

        List<ProjectMessageDto> messages = memberView
                ? projectMessageRepository.findByProjectIdOrderByCreatedAtAsc(projectId).stream().map(this::toMessageDto).toList()
                : List.of();

        List<ProjectTaskDto> tasks = memberView
                ? projectTaskRepository.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(this::toTaskDto).toList()
                : List.of();

        List<JoinRequestDto> joinRequests = ownerView
                ? projectJoinRequestRepository.findByProjectIdOrderByCreatedAtDesc(projectId).stream().map(this::toJoinRequestDto).toList()
                : List.of();

        ProjectDtoRes projectDto = projectMapper.todto(project);
        projectDto.setMatchScore(calculateMatchScore(projectDto, currentUser));
        return new ProjectWorkspaceDto(projectDto, ownerView, memberView, pendingRequest, messages, tasks, joinRequests);
    }

    public ProjectMessageDto addMessage(Long projectId, CreateMessageDto request) {
        User currentUser = getCurrentUser();
        Project project = getProject(projectId);
        ensureProjectMember(project, currentUser);

        ProjectMessage message = new ProjectMessage();
        message.setProject(project);
        message.setSender(currentUser);
        message.setContent(request.getContent().trim());
        ProjectMessage saved = projectMessageRepository.save(message);

        currentUser.setContributionScore(Math.min(100, currentUser.getContributionScore() + 1));
        userService.logContributionHistory(currentUser);
        return toMessageDto(saved);
    }

    public ProjectTaskDto addTask(Long projectId, CreateTaskDto request) {
        User currentUser = getCurrentUser();
        Project project = getProject(projectId);
        ensureProjectMember(project, currentUser);

        ProjectTask task = new ProjectTask();
        task.setProject(project);
        task.setCreatedBy(currentUser);
        task.setTitle(request.getTitle().trim());
        task.setDescription(request.getDescription());
        task.setStartDate(request.getStartDate());
        task.setDeadline(request.getDeadline());
        task.setTags(request.getTags());

        if (request.getAssignedToId() != null) {
            User assignedTo = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFound("Assigned user not found"));
            task.setAssignedTo(assignedTo);
        }

        ProjectTask saved = projectTaskRepository.save(task);
        return toTaskDto(saved);
    }

    public ProjectTaskDto updateTaskStatus(Long taskId, TaskStatus status) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFound("Task not found"));
        User currentUser = getCurrentUser();
        ensureProjectMember(task.getProject(), currentUser);

        task.setStatus(status);
        if (status == TaskStatus.DONE) {
            currentUser.setContributionScore(Math.min(100, currentUser.getContributionScore() + 2));
            userService.logContributionHistory(currentUser);
        }
        return toTaskDto(projectTaskRepository.save(task));
    }

    public DashboardSummaryDto getDashboardSummary() {
        User currentUser = getCurrentUser();
        UserProfileResponseDto profile = userService.getMyProfile();
        List<ProjectDtoRes> projects = getAllUserProjects().stream()
                .sorted(Comparator.comparing(ProjectDtoRes::getCreatedAt).reversed())
                .toList();

        List<ProjectTaskDto> tasks = projects.stream()
                .flatMap(project -> projectTaskRepository.findByProjectIdOrderByCreatedAtDesc(project.getId()).stream())
                .map(this::toTaskDto)
                .limit(12)
                .toList();

        int ownedProjects = (int) projects.stream()
                .filter(project -> project.getCreatedByName() != null && project.getCreatedByName().equals(currentUser.getName()))
                .count();

        int pendingApprovals = projects.stream()
                .filter(project -> project.getCreatedByName() != null && project.getCreatedByName().equals(currentUser.getName()))
                .mapToInt(project -> (int) projectJoinRequestRepository.findByProjectIdOrderByCreatedAtDesc(project.getId()).stream()
                        .filter(request -> request.getStatus() == JoinRequestStatus.PENDING)
                        .count())
                .sum();

        int tasksDue = (int) tasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .count();

        List<ContributionPointDto> history = contributionHistoryRepository.findByUserOrderByTimestampAsc(currentUser).stream()
                .map(h -> new ContributionPointDto(h.getScore(), h.getTimestamp()))
                .toList();

        if (history.isEmpty()) {
            userService.logContributionHistory(currentUser);
            history = List.of(new ContributionPointDto(currentUser.getContributionScore(), LocalDateTime.now()));
        }

        return new DashboardSummaryDto(profile, projects.size(), ownedProjects, pendingApprovals, tasksDue, projects, tasks, history);
    }



    public void deleteOwnProject(Long projectId) {
        User user = getCurrentUser();
        Project project = getProject(projectId);

        if (!project.getCreatedBy().getId().equals(user.getId())) {
            throw new SecurityException("Not allowed to delete this project");
        }

        projectRepository.delete(project);
    }


    public void adminDeleteProject(Long id) {
        projectRepository.delete(getProject(id));
    }

    private User getCurrentUser() {
        String email = AuthUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFound("User not found"));
    }

    private Project getProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFound("Project not found"));
    }

    private ProjectJoinRequest getJoinRequest(Long requestId) {
        return projectJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFound("Join request not found"));
    }

    private void ensureProjectMember(Project project, User currentUser) {
        boolean allowed = project.getCreatedBy().getId().equals(currentUser.getId()) ||
                project.getParticipants().stream().anyMatch(user -> user.getId().equals(currentUser.getId()));
        if (!allowed) {
            throw new SecurityException("You need to be a project member to access the workspace");
        }
    }

    private ProjectMessageDto toMessageDto(ProjectMessage message) {
        return new ProjectMessageDto(
                message.getId(),
                message.getSender().getId(),
                message.getSender().getName(),
                message.getSender().getContributionScore(),
                message.getContent(),
                message.getCreatedAt()
        );
    }

    private ProjectTaskDto toTaskDto(ProjectTask task) {
        return new ProjectTaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedBy().getId(),
                task.getCreatedBy().getName(),
                task.getAssignedTo() != null ? task.getAssignedTo().getId() : null,
                task.getAssignedTo() != null ? task.getAssignedTo().getName() : null,
                task.getCreatedAt(),
                task.getStartDate(),
                task.getDeadline(),
                task.getProject().getId(),
                task.getProject().getTitle(),
                task.getTags()
        );
    }

    private JoinRequestDto toJoinRequestDto(ProjectJoinRequest request) {
        User requester = request.getRequester();
        List<ContributionPointDto> history = contributionHistoryRepository.findByUserOrderByTimestampAsc(requester).stream()
                .map(h -> new ContributionPointDto(h.getScore(), h.getTimestamp()))
                .toList();

        if (history.isEmpty()) {
            history = List.of(new ContributionPointDto(requester.getContributionScore(), requester.getCreatedAt()));
        }

        return new JoinRequestDto(
                request.getId(),
                requester.getId(),
                requester.getName(),
                requester.getEmail(),
                requester.getContributionScore(),
                request.getNote(),
                request.getStatus(),
                request.getCreatedAt(),
                requester.getBio(),
                requester.getGithubUrl(),
                requester.getSkills(),
                requester.getJoinedProject().size() + requester.getCreatedProjects().size(),
                history,
                request.getProject().getId(),
                request.getProject().getTitle(),
                request.getInvitedByOwner(),
                Boolean.TRUE.equals(request.getInvitedByOwner()) ? request.getProject().getCreatedBy().getName() : null
        );
    }

    private long calculateMatchScore(ProjectDtoRes project, User currentUser) {
        if (currentUser.getSkills() == null || project.getTechStack() == null) {
            return 0;
        }
        return project.getTechStack().stream()
                .filter(currentUser.getSkills()::contains)
                .count();
    }
}
