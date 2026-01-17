package com.example.service.validation;

import com.example.common.dto.UserBaseRequest;
import com.example.common.validation.IValidator;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

@Component
public class ProfileValidator implements IValidator<UserBaseRequest> {

    @Override
    public void validate(UserBaseRequest request) {
        validateName(request.getFirstName(), "First Name");
        validateName(request.getLastName(), "Last Name");

        // Age validation (13+)
        if (request.getDateOfBirth() != null) {
            if (Period.between(request.getDateOfBirth(), LocalDate.now()).getYears() < 13) {
                throw new RuntimeException("User must be at least 13 years old");
            }
        }
    }

    private void validateName(String name, String field) {
        if (name == null || name.trim().length() < 2 || name.trim().length() > 50) {
            throw new RuntimeException(field + " must be between 2 and 50 characters");
        }
        if (!Pattern.matches("^[a-zA-Z\\s\\-\\'\\.]+$", name)) {
            throw new RuntimeException(field + " contains invalid characters");
        }
    }
}
