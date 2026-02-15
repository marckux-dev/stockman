package com.marckux.stockman.shared.domain.constants;

public class ValidationConstants {

  private ValidationConstants(){}

  public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
  public static final String BCRYPT_HASH_PATTERN = "^\\$2[ayb]\\$.{56}$";
}
