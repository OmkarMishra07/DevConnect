package com.example.MergeX.controller;

import com.example.MergeX.Dto.LoginRequestDto;
import com.example.MergeX.Dto.LoginResponseDto;
import com.example.MergeX.Dto.SignUpDTOReq;
import com.example.MergeX.Dto.SignupResponseDto;
import com.example.MergeX.Security.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request){
        return ResponseEntity.ok(authService.login(request));
    }
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@Valid @RequestBody SignUpDTOReq request){
        return ResponseEntity.ok(authService.signup(request));
    }
}
