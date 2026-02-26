package com.marckux.stockman.auth.domain.model.vo;

import java.util.regex.Pattern;

import com.marckux.stockman.shared.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.shared.domain.constants.ValidationConstants;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class Email {

  public static final Pattern VALIDATION_PATTERN = Pattern.compile(ValidationConstants.EMAIL_REGEX);

  private final String value;

  public Email(String value) {
    if (value == null || value.isBlank())
      throw new InvalidAttributeException("El email no puede estar vacío");
    if (!VALIDATION_PATTERN.matcher(value).matches())
      throw new InvalidAttributeException("El formato del email no es válido");
    this.value = value;
  }

  public static Email of(String value) {
    return new Email(value);
  }

}
