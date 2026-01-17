package com.example.service.validation;

import com.example.common.dto.UserUpdateRequest;
import com.example.common.entity.User;
import com.example.common.repository.IUserRepository;
import com.example.common.validation.IValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProfileUpdateValidator implements IValidator<UserUpdateRequest> {

    private final IUserRepository userRepository;
    private String currentUserId; // This needs to be set before validation

    public void setCurrentUserId(String id) {
        this.currentUserId = id;
    }

    @Override
    public void validate(UserUpdateRequest request) {
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            Optional<User> existing = userRepository.findByPhoneNumber(request.getPhoneNumber());
            if (existing.isPresent() && !existing.get().getId().equals(currentUserId)) {
                throw new RuntimeException("Phone number already in use by another user");
            }
        }
    }
}
