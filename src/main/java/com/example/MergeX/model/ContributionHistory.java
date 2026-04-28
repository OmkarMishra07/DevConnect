package com.example.MergeX.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "contribution_history")
public class ContributionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public ContributionHistory(User user, Integer score) {
        this.user = user;
        this.score = score;
    }
}
