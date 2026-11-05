package com.foodieexpress.service;

import com.foodieexpress.dto.UserLoginDTO;
import com.foodieexpress.dto.UserRegisterDTO;
import com.foodieexpress.dto.UserResponseDTO;
import com.foodieexpress.model.User;

public interface UserService {
    UserResponseDTO register(UserRegisterDTO dto);
    UserResponseDTO login(UserLoginDTO dto);
    User getUserById(Long id);
}

