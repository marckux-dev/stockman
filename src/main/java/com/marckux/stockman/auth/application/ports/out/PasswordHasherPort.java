package com.marckux.stockman.auth.application.ports.out;

public interface PasswordHasherPort {

  public String encode(String password);
  public boolean matches(String rawPassword, String hashedPassword);
}
