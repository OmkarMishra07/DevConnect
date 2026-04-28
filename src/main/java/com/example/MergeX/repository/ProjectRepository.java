package com.example.MergeX.repository;

import com.example.MergeX.Dto.ProjectDtoRes;
import com.example.MergeX.model.Project;
import com.example.MergeX.model.ProjectStatus;
import com.example.MergeX.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Set<ProjectDtoRes> getAllByCreatedBy(User user);

    Set<Project> findAllByParticipantsContaining(User user);

    Collection<? extends Project> findAllByCreatedBy(User user);

    Page<Project> findByStatus(ProjectStatus status, org.springframework.data.domain.Pageable pageable);

    Page<Project> findByTechStackContaining(String tech, org.springframework.data.domain.Pageable pageable);

    Page<Project> findByStatusAndTechStackContaining(
            ProjectStatus status,
            String tech,
            Pageable pageable);

    Project findProjectById(Long id);

    Collection<Project> findByCreatedBy(User user);

    @Query("""
       SELECT p FROM Project p
       WHERE p.createdBy.id <> :userId
       AND :user NOT MEMBER OF p.participants
       AND (p.maxTeamSize IS NULL OR (SELECT COUNT(m) FROM p.participants m) < p.maxTeamSize)
       AND (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.tagline) LIKE LOWER(CONCAT('%', :keyword, '%')))
       """)
    Page<Project> findExploreProjects(
            @Param("userId") Long userId,
            @Param("user") User user,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}