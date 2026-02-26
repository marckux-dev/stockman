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
public class HashedPassword {

  private static final Pattern BCRYPT_PATTERN = Pattern.compile(ValidationConstants.BCRYPT_HASH_PATTERN);
  private final String value;


  public HashedPassword(String value) {
    if (value == null) 
      throw new InvalidAttributeException("La password no puede ser vacía");
    if (!BCRYPT_PATTERN.matcher(value).matches())
      throw new InvalidAttributeException("Solo se puede asignar password hashedada");
    this.value = value;
  }

  public static HashedPassword of(String value) {
    return new HashedPassword(value);
  }

  
}
