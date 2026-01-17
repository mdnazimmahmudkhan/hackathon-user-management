package com.example.common.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserCreateRequest extends UserBaseRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 10)
    private String password;
}
