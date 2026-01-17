package com.example.common.repository;

import com.example.common.entity.User;
import java.util.Optional;

public interface IUserRepository extends IRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    com.example.common.dto.PaginatedResponse<User> searchUsers(
            String keyword,
            boolean includeInactive,
            boolean includeDeleted,
            int page,
            int size);
}
