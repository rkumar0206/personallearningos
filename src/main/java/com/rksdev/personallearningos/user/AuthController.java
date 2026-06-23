package com.rksdev.personallearningos.user;

import com.rksdev.personallearningos.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @GetMapping("/confirm")
    public ResponseEntity<?> activateAccount(@RequestParam("token") String token) {
        return userRepository.findByVerificationToken(token)
                .map(user -> {
                    user.setEnabled(true);
                    user.setVerificationToken(null); // Consume token
                    userRepository.save(user);
                    return ResponseEntity.ok(Map.of("message", "Account activated successfully! You may now log in."));
                })
                .orElseGet(() -> ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired activation token.")));
    }
}
