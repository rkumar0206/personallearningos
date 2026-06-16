package com.rksdev.personallearningos.user.service;

import com.rksdev.personallearningos.user.enums.Roles;
import com.rksdev.personallearningos.user.model.UserEntity;
import com.rksdev.personallearningos.user.repository.UserRepository;
import com.rksdev.security.api.PluggableUserRegistrationHandler;
import com.rksdev.security.dto.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AppRegistrationHandler implements PluggableUserRegistrationHandler {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public Object handleUserSignUp(SignUpRequest request) {
        // 1. Enforce unique constraints gracefully
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username is already taken.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        // 2. Build and persist the new learner identity
        UserEntity user = UserEntity.builder()
                .username(request.username())
                .email(request.email())
                .password(request.password()) // Note: Password is already pre-hashed by the library controller
                .roles(Set.of(Roles.LEARNER.getValue())) // Standard default authorization level
                .build();

        userRepository.save(user);

        return Map.of(
                "status", "SUCCESS",
                "message", "Your Learning OS profile has been created successfully."
        );
    }
}