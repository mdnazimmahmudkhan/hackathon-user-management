package com.example.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true, sparse = true)
    private String phoneNumber;

    private String password;
    private String displayName;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
    private boolean deleted;
}
