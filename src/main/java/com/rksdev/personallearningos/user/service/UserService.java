package com.rksdev.personallearningos.user.service;

import com.rksdev.personallearningos.user.dto.EditRoleRequestDTO;
import com.rksdev.personallearningos.user.dto.UserResponse;

public interface UserService {

    UserResponse getCurrentUserDetails(Long id);

    UserResponse editRoles(EditRoleRequestDTO editRoleRequestDTO);
}
