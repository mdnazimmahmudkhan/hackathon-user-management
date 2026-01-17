package com.example.service;

import com.example.common.dto.UserCreateRequest;
import com.example.common.dto.UserResponse;
import com.example.common.dto.UserUpdateRequest;
import com.example.common.entity.User;
import com.example.common.repository.IUserRepository;
import com.example.common.service.IUserService;
import com.example.common.validation.IValidator;
import com.example.service.validation.ProfileUpdateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final org.springframework.mail.javamail.JavaMailSender mailSender;
    private final List<IValidator<? super UserCreateRequest>> registrationValidators;
    private final List<IValidator<? super UserUpdateRequest>> updateValidators;

    @Override
    public UserResponse registerUser(UserCreateRequest request) {
        // Run registration validators
        registrationValidators.forEach(v -> v.validate(request));

        String normalizedEmail = request.getEmail().trim().toLowerCase();
        String normalizedPhone = normalizePhone(request.getPhoneNumber());

        User user = User.builder()
                .email(normalizedEmail)
                .phoneNumber(normalizedPhone)
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .displayName(request.getDisplayName())
                .dateOfBirth(request.getDateOfBirth())
                .createdAt(LocalDateTime.now())
                .active(true)
                .deleted(false)
                .build();

        User savedUser = userRepository.add(user);
        
        sendWelcomeEmail(savedUser);
        
        return mapToResponse(savedUser);
    }

    private void sendWelcomeEmail(User user) {
        try {
            org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Welcome to User Management System!");
            message.setText(String.format("Hello %s,\n\nYour account has been successfully created.\n\nBest regards,\nUser Management Team", 
                user.getFirstName()));
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't fail registration
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }


    @Override
    public UserResponse updateUser(String id, UserUpdateRequest request) {
        User user = userRepository.getById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isDeleted()) {
            throw new RuntimeException("Cannot update a deleted user");
        }

        // Set context for ID-aware validators (Identity checks)
        updateValidators.forEach(v -> {
            if (v instanceof ProfileUpdateValidator puv) {
                puv.setCurrentUserId(id);
            }
            v.validate(request);
        });

        String normalizedPhone = normalizePhone(request.getPhoneNumber());

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setDisplayName(request.getDisplayName());
        user.setPhoneNumber(normalizedPhone);
        user.setDateOfBirth(request.getDateOfBirth());
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.update(user);
        return mapToResponse(updatedUser);
    }

    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) return null;
        
        String clean = phone.replaceAll("[^0-9]", "");
        // Bangladesh normalization example: +8801XXXXXXXXX
        if (clean.startsWith("880") && clean.length() == 13) {
            return "+" + clean;
        } else if (clean.startsWith("0") && clean.length() == 11) {
            return "+88" + clean;
        } else if (clean.length() == 10) {
            return "+880" + clean;
        }
        
        return "+" + clean; // Fallback
    }

    @Override
    public UserResponse getUserById(String id) {
        return userRepository.getById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public Collection<UserResponse> listAllUsers() {
        return userRepository.listAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .displayName(user.getDisplayName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .createdAt(user.getCreatedAt())
                .active(user.isActive())
                .build();
    }

    @Override
    public com.example.common.dto.PaginatedResponse<UserResponse> searchUsers(String keyword, boolean includeInactive, boolean includeDeleted, int page, int size) {
        com.example.common.dto.PaginatedResponse<User> result = userRepository.searchUsers(keyword, includeInactive, includeDeleted, page, size);
        
        List<UserResponse> responseItems = result.getItems().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return com.example.common.dto.PaginatedResponse.<UserResponse>builder()
                .items(responseItems)
                .totalItems(result.getTotalItems())
                .totalPages(result.getTotalPages())
                .currentPage(result.getCurrentPage())
                .pageSize(result.getPageSize())
                .build();
    }
}
