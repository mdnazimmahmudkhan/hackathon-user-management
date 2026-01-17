package com.example.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String email;
    private String phoneNumber;
    private String displayName;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private LocalDateTime createdAt;
    private boolean active;
}
