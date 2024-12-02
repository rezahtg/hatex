package com.reza.hatex.utils;

import com.reza.hatex.helper.ValidPassword;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (password == null) return false;

        boolean hasUpperCase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowerCase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasNumber = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecialChar = password.chars().anyMatch(ch -> "!@#$%^&*()_+[]{}|;':\",.<>?".indexOf(ch) >= 0);
        boolean hasMinLength = password.length() >= 8;

        return hasUpperCase && hasLowerCase && hasNumber && hasSpecialChar && hasMinLength;
    }
}
