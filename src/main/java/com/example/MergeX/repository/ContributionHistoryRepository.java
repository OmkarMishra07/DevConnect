package com.example.MergeX.repository;

import com.example.MergeX.model.ContributionHistory;
import com.example.MergeX.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContributionHistoryRepository extends JpaRepository<ContributionHistory, Long> {
    List<ContributionHistory> findByUserOrderByTimestampAsc(User user);
}
