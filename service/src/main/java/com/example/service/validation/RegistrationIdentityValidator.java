package com.example.service.validation;

import com.example.common.dto.UserCreateRequest;
import com.example.common.repository.IUserRepository;
import com.example.common.validation.IValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrationIdentityValidator implements IValidator<UserCreateRequest> {

    private final IUserRepository userRepository;

    @Override
    public void validate(UserCreateRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Email already in use");
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new RuntimeException("Phone number already in use");
            }
        }
    }
}
