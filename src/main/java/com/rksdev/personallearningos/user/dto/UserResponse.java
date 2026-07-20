package com.rksdev.personallearningos.user.dto;

import java.util.ArrayList;
import java.util.Set;

public record UserResponse(
        String username,
        String email,
        boolean enabled,
        Set<String> roles
) { }