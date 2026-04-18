package com.example.MergeX.Security;

import com.example.MergeX.model.User;
import com.example.MergeX.model.UserCredentials;
import com.example.MergeX.repository.UserInfoRepository;
import com.example.MergeX.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentDetailService implements UserDetailsService {

    private final UserInfoRepository credentialsRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        UserCredentials creds = credentialsRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Invalid email or password"));

        return org.springframework.security.core.userdetails.User
                .withUsername(creds.getEmail())
                .password(creds.getPassword())
                .authorities("ROLE_" + creds.getRole().name())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
