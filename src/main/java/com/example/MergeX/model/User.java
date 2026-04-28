package com.example.MergeX.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false,unique = true)
    private String email;
    private String githubUrl;
    private String domain;
    @Enumerated(EnumType.STRING)
    private Education education;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id", referencedColumnName = "id")
    private College college;
    @ElementCollection
    @CollectionTable(name = "skills",joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "skill_name")
    private Set<String> skills = new HashSet<>();
    @Column(length = 500)
    private String bio;
    private LocalDateTime createdAt = LocalDateTime.now();
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Project> createdProjects = new HashSet<>();
    @ManyToMany(mappedBy = "participants")
    @JsonIgnore
    private Set<Project> joinedProject = new HashSet<>();
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;
    @Column(nullable = false)
    private boolean profileCompleted = false;
    @Column(nullable = false)
    private Integer contributionScore = 50;


    public boolean getProfileCompleted() {
        return profileCompleted;
    }
}
