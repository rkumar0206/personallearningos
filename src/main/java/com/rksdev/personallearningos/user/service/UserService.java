package com.rksdev.personallearningos.user.service;

import com.rksdev.personallearningos.user.dto.UserResponse;

public interface UserService {

    UserResponse getCurrentUserDetails(Long id);
}
