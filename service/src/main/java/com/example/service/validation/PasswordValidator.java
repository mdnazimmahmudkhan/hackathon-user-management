package com.example.service.validation;

import com.example.common.dto.UserCreateRequest;
import com.example.common.validation.IValidator;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordValidator implements IValidator<UserCreateRequest> {

    @Override
    public void validate(UserCreateRequest request) {
        String password = request.getPassword();
        String email = request.getEmail();
        String phone = request.getPhoneNumber();

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
}
