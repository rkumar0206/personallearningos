package com.rksdev.personallearningos.user.service;

import com.rksdev.personallearningos.user.model.PasswordResetTokenEntity;
import com.rksdev.personallearningos.user.model.UserEntity;
import com.rksdev.personallearningos.user.repository.PasswordResetTokenRepository;
import com.rksdev.personallearningos.user.repository.UserRepository;
import com.rksdev.security.api.PluggablePasswordResetHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppPasswordResetHandler implements PluggablePasswordResetHandler {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository resetTokenRepository;

    @Override
    @Transactional
    public void handleResetRequested(String email, String secureToken) {
        // Look up by email (or username field depending on alignment configuration)
        Optional<UserEntity> userOpt = userRepository.findByUsernameOrEmail(email, email);

        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();

            // Clear any lingering reset requests for this specific user
            resetTokenRepository.deleteByUser(user);

            // Set token to expire exactly 15 minutes from now
            Instant expiration = Instant.now().plus(15, ChronoUnit.MINUTES);

            PasswordResetTokenEntity resetEntity = PasswordResetTokenEntity.builder()
                    .token(secureToken)
                    .user(user)
                    .expiryDate(expiration)
                    .build();

            resetTokenRepository.save(resetEntity);

            //todo: write email service to send the email

            // Mocking email dispatch out to console. Swap with EmailService down the line.
            System.out.printf("[EMAIL DISPATCH] To: %s | Subject: Reset Your Password%n", user.getEmail());
            System.out.printf("[EMAIL BODY] Use token to complete reset: %s%n", secureToken);
        } else {
            // Log missing requests internally for debugging; endpoint still returns safe generic messages to prevent user tracing
            System.out.printf("[WARN] Password reset requested for non-existent account: %s%n", email);
        }
    }

    @Override
    @Transactional
    public boolean handlePasswordReset(String token, String encryptedNewPassword) {
        Optional<PasswordResetTokenEntity> tokenEntityOpt = resetTokenRepository.findByToken(token);

        // 1. Verify token exists and hasn't passed its expiry checkpoint
        if (tokenEntityOpt.isEmpty() || tokenEntityOpt.get().getExpiryDate().isBefore(Instant.now())) {
            return false;
        }

        PasswordResetTokenEntity tokenEntity = tokenEntityOpt.get();
        UserEntity user = tokenEntity.getUser();

        // 2. Commit the new password hash to the user record
        user.setPassword(encryptedNewPassword);
        userRepository.save(user);

        // 3. Immediately consume and discard the token so it cannot be re-used
        resetTokenRepository.delete(tokenEntity);

        return true;
    }
}