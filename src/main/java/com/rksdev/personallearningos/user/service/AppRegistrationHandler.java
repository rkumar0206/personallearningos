package com.rksdev.personallearningos.user.service;

import com.rksdev.personallearningos.shared.exception.DuplicateResourceInDbException;
import com.rksdev.personallearningos.shared.service.EmailService;
import com.rksdev.personallearningos.shared.util.AppUtils;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppRegistrationHandler implements PluggableUserRegistrationHandler {

    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public Object handleUserSignUp(SignUpRequest request) {
        // 1. Enforce unique constraints gracefully
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new DuplicateResourceInDbException("Username is already taken.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceInDbException("Email is already registered.");
        }

        // 2. Build and persist the new learner identity
        UserEntity user = UserEntity.builder()
                .username(request.username())
                .email(request.email())
                .password(request.password()) // Note: Password is already pre-hashed by the library controller
                .roles(Set.of(Roles.LEARNER.getValue())) // Standard default authorization level
                .enabled(false)
                .build();

        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);

        userRepository.save(user);

        String confirmationLink = AppUtils.getBaseUrl() + "/api/v1/auth/confirm?token=" + token;
        emailService.sendAccountConfirmationEmail(user.getEmail(), confirmationLink);

        return Map.of(
                "message", "Registration successful! Please check your inbox to verify your account.",
                "username", user.getUsername()
        );
    }
}