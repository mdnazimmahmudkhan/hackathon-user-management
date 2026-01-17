package com.example.common.service;

import com.example.common.dto.UserCreateRequest;
import com.example.common.dto.UserResponse;
import java.util.Collection;

public interface IUserService {
    UserResponse registerUser(UserCreateRequest request);
    UserResponse getUserById(String id);
    Collection<UserResponse> listAllUsers();
}
