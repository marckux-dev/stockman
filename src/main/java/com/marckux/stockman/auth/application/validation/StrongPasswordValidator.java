package com.marckux.stockman.auth.application.validation;

import java.util.regex.Pattern;

import com.marckux.stockman.shared.domain.constants.ValidationConstants;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String>{

  private static final Pattern PATTERN = Pattern.compile(ValidationConstants.PASSWORD_REGEX);

  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    if (password == null)
      return false;
    return PATTERN.matcher(password).matches();
  }

}
