package com.example.MergeX.service;

import com.example.MergeX.Dto.*;
import com.example.MergeX.Security.AuthUtil;
import com.example.MergeX.exceptions.ResourceNotFound;
import com.example.MergeX.mapper.UserMapper;
import com.example.MergeX.model.College;
import com.example.MergeX.model.ContributionHistory;
import com.example.MergeX.model.User;
import com.example.MergeX.repository.CollegeRepository;
import com.example.MergeX.repository.ContributionHistoryRepository;
import com.example.MergeX.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final CollegeRepository collegeRepository;
    private final BCryptPasswordEncoder Encoder;
    private final UserMapper userMapper;
    private final ContributionHistoryRepository contributionHistoryRepository;

    @Transactional
    public void completeProfile(CompleteProfileRequestDto request) {
        String email = AuthUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEducation(request.getEducation());
        user.setGithubUrl(request.getGithubUrl());
        user.setBio(request.getBio());
        user.setSkills(request.getSkills());
        user.setDomain(request.getDomain());

        if (request.getCollegeName() != null) {
            College college = collegeRepository.findByName(request.getCollegeName())
                    .orElseGet(() -> {
                        College newCollege = new College();
                        newCollege.setName(request.getCollegeName());
                        return collegeRepository.save(newCollege);
                    });
            user.setCollege(college);
        }

        user.setProfileCompleted(true);
        userRepository.save(user);
        logContributionHistory(user);
    }

    public UserProfileResponseDto getMyProfile() {
        String email = AuthUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean profileComplete = user.getEducation() != null &&
                user.getCollege() != null &&
                user.getBio() != null &&
                user.getGithubUrl() != null;
        user.setProfileCompleted(profileComplete);

        return new UserProfileResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getEducation(),
                user.getCollege() != null ? user.getCollege().getName() : null,
                user.getDomain(),
                user.getSkills(),
                user.getGithubUrl(),
                user.getBio(),
                user.getContributionScore(),
                user.getProfileCompleted()
        );
    }

    @Transactional
    public void logContributionHistory(User user) {
        contributionHistoryRepository.save(new ContributionHistory(user, user.getContributionScore()));
    }

    public List<UserDtoRes> getAllUsers() {
        return userRepository.findAll().stream().map(u -> populateHistory(userMapper.todto(u), u)).toList();
    }

    public List<UserDtoRes> searchUsersBySkill(String skill) {
        return userRepository.findBySkill(skill).stream()
                .map(u -> populateHistory(userMapper.todto(u), u))
                .toList();
    }

    public UserDtoRes getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("User not found"));
        return populateHistory(userMapper.todto(user), user);
    }

    private UserDtoRes populateHistory(UserDtoRes dto, User user) {
        List<ContributionPointDto> history = contributionHistoryRepository.findByUserOrderByTimestampAsc(user).stream()
                .map(h -> new ContributionPointDto(h.getScore(), h.getTimestamp()))
                .toList();
        dto.setContributionHistory(history);
        dto.setContributionScore(user.getContributionScore());
        return dto;
    }
}
