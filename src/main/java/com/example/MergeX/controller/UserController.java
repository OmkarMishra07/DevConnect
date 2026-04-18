package com.example.MergeX.controller;

import com.example.MergeX.Dto.CompleteProfileRequestDto;
import com.example.MergeX.Dto.UserDtoReq;
import com.example.MergeX.Dto.UserDtoRes;
import com.example.MergeX.Dto.UserProfileResponseDto;
import com.example.MergeX.model.User;
import com.example.MergeX.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/complete-profile")
    public ResponseEntity<String> completeProfile(
            @RequestBody CompleteProfileRequestDto request) {

        userService.completeProfile(request);
        return ResponseEntity.ok("Profile completed successfully");
    }

    @GetMapping("/getall")
    public ResponseEntity<List<UserDtoRes>> getall(){

        return ResponseEntity.ok(userService.getAllUsers());
    }
    @GetMapping("/getuser/{id}")
    public ResponseEntity<UserDtoRes> getuser(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

}
