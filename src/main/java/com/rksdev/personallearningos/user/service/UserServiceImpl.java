package com.rksdev.personallearningos.user.service;

import com.rksdev.personallearningos.user.CustomUserDetails;
import com.rksdev.personallearningos.user.dto.UserResponse;
import com.rksdev.personallearningos.user.model.UserEntity;
import com.rksdev.personallearningos.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + username));

        return new CustomUserDetails(user);
    }

    @Override
    public UserResponse getCurrentUserDetails(Long id) {
        return userRepository.findById(id)
                .map(u -> new UserResponse(u.getUsername(), u.getEmail(), u.isEnabled(), u.getRoles()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + id));
    }
}