package com.rksdev.personallearningos.user.controller;

import com.rksdev.personallearningos.user.dto.UserResponse;
import com.rksdev.personallearningos.user.service.UserService;
import com.rksdev.security.web.CurrentUserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getCurrentUserDetails(@CurrentUserId Long userId) {
        return ResponseEntity.ok(userService.getCurrentUserDetails(userId));
    }
}
