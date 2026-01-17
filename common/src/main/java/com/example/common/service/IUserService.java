package com.example.common.service;

import com.example.common.dto.UserCreateRequest;
import com.example.common.dto.UserResponse;
import com.example.common.dto.UserUpdateRequest;
import java.util.Collection;

public interface IUserService {
    UserResponse registerUser(UserCreateRequest request);

    UserResponse updateUser(String id, UserUpdateRequest request);

    UserResponse getUserById(String id);

    Collection<UserResponse> listAllUsers();

    com.example.common.dto.PaginatedResponse<UserResponse> searchUsers(
            String keyword,
            boolean includeInactive,
            boolean includeDeleted,
            int page,
            int size);
}
