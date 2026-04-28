package com.example.MergeX.repository;

import com.example.MergeX.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN u.skills s WHERE " +
            "LOWER(s) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(u.domain) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<User> findBySkill(@Param("query") String query);
}