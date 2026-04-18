package com.example.MergeX.repository;

import com.example.MergeX.model.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserCredentials, Long> {

    Optional<UserCredentials> findByEmail(String email);
}