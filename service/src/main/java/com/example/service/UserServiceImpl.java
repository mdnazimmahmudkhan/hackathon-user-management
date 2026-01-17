package com.example.service;

import com.example.common.dto.UserCreateRequest;
import com.example.common.dto.UserResponse;
import com.example.common.entity.User;
import com.example.common.repository.IUserRepository;
import com.example.common.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse registerUser(UserCreateRequest request) {
        validateRequest(request);

        String normalizedEmail = request.getEmail().trim().toLowerCase();
        String normalizedPhone = normalizePhone(request.getPhoneNumber());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Email already in use");
        }
        if (normalizedPhone != null && userRepository.existsByPhoneNumber(normalizedPhone)) {
            throw new RuntimeException("Phone number already in use");
        }

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
        return mapToResponse(savedUser);
    }

    private void validateRequest(UserCreateRequest request) {
        // Name validation
        validateName(request.getFirstName(), "First Name");
        validateName(request.getLastName(), "Last Name");

        // Age validation (13+)
        if (request.getDateOfBirth() != null) {
            if (Period.between(request.getDateOfBirth(), LocalDate.now()).getYears() < 13) {
                throw new RuntimeException("User must be at least 13 years old");
            }
        }

        // Password complexity
        validatePassword(request.getPassword(), request.getEmail(), request.getPhoneNumber());
    }

    private void validateName(String name, String field) {
        if (name == null || name.trim().length() < 2 || name.trim().length() > 50) {
            throw new RuntimeException(field + " must be between 2 and 50 characters");
        }
        if (!Pattern.matches("^[a-zA-Z\\s\\-\\'\\.]+$", name)) {
            throw new RuntimeException(field + " contains invalid characters");
        }
    }

    private void validatePassword(String password, String email, String phone) {
        if (password.length() < 10) {
            throw new RuntimeException("Password must be at least 10 characters");
        }
        boolean hasUpper = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasLower = Pattern.compile("[a-z]").matcher(password).find();
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find();

        if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
            throw new RuntimeException("Password must contain uppercase, lowercase, number, and special character");
        }

        // Substring checks
        String emailPrefix = email.split("@")[0].toLowerCase();
        if (password.toLowerCase().contains(emailPrefix)) {
            throw new RuntimeException("Password cannot contain email prefix");
        }

        if (phone != null && phone.length() >= 6) {
            String phoneSuffix = phone.substring(phone.length() - 6);
            if (password.contains(phoneSuffix)) {
                throw new RuntimeException("Password cannot contain mobile number substring");
            }
        }
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
}
