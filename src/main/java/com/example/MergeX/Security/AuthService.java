package com.example.MergeX.Security;

import com.example.MergeX.Dto.LoginRequestDto;
import com.example.MergeX.Dto.LoginResponseDto;
import com.example.MergeX.Dto.SignUpDTOReq;
import com.example.MergeX.Dto.SignupResponseDto;
import com.example.MergeX.model.Role;
import com.example.MergeX.model.User;
import com.example.MergeX.model.UserCredentials;
import com.example.MergeX.repository.UserInfoRepository;
import com.example.MergeX.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public LoginResponseDto login(LoginRequestDto request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails user = (UserDetails) authentication.getPrincipal();
            assert user != null;
            String email = user.getUsername();
            UserCredentials credentials = userInfoRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            String token = authUtil.generateAccessToken(credentials);
            return new LoginResponseDto(token, credentials.getId());

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }

    public SignupResponseDto signup(SignUpDTOReq request) {
        if (userInfoRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        User savedUser = userRepository.save(user);
        System.out.println("Signup started for: " + request.getEmail());
        UserCredentials creds = new UserCredentials();
        creds.setUser(savedUser);
        creds.setEmail(request.getEmail());
        creds.setPassword(passwordEncoder.encode(request.getPassword()));
        creds.setRole(Role.USER);

        userInfoRepository.save(creds);

        return new SignupResponseDto(creds.getId(), creds.getEmail());
    }
}
