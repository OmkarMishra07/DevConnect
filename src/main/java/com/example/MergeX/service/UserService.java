package com.example.MergeX.service;

import com.example.MergeX.Dto.CompleteProfileRequestDto;
import com.example.MergeX.Dto.UserDtoReq;
import com.example.MergeX.Dto.UserDtoRes;
import com.example.MergeX.Dto.UserProfileResponseDto;
import com.example.MergeX.Security.AuthUtil;
import com.example.MergeX.exceptions.ResourceNotFound;
import com.example.MergeX.mapper.UserMapper;
import com.example.MergeX.model.College;
import com.example.MergeX.model.User;
import com.example.MergeX.repository.CollegeRepository;
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
    private  final BCryptPasswordEncoder Encoder;
    private final UserMapper userMapper;

    @Transactional
    public void completeProfile(CompleteProfileRequestDto request) {

        String email = AuthUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEducation(request.getEducation());
        user.setGithubUrl(request.getGithubUrl());
        user.setBio(request.getBio());
        user.setSkills(request.getSkills());

        if (request.getCollegeName() != null) {
            College college = collegeRepository.findByName(request.getCollegeName())
                    .orElseThrow(() -> new RuntimeException("College not found"));
            user.setCollege(college);
        }

        user.setProfileCompleted(true);
        userRepository.save(user);

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
                user.getSkills(),
                user.getGithubUrl(),
                user.getBio(),
                user.getProfileCompleted()
        );
    }

    public List<UserDtoRes> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::todto).toList();
    }

    public UserDtoRes getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("User not found"));
        return userMapper.todto(user);
    }

}
