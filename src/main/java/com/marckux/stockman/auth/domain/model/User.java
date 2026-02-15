package com.marckux.stockman.auth.domain.model;

import java.util.UUID;

import com.marckux.stockman.auth.domain.exceptions.InvalidAttributeException;
import com.marckux.stockman.auth.domain.model.vo.Email;
import com.marckux.stockman.auth.domain.model.vo.HashedPassword;

import lombok.Builder;
import lombok.Getter;

@Getter
public class User {

  UUID id;
  Email email;
  String name;
  HashedPassword hashedPassword;
  Role role;
  Boolean isActive;


  @Builder(toBuilder = true)
  public User(UUID id, Email email, String name, HashedPassword hashedPassword, Role role, Boolean isActive) {
    if (email == null)
      throw new InvalidAttributeException("El email no puede estar vacío");
    if (hashedPassword == null)
      throw new InvalidAttributeException("La password no puede estar vacía");
    this.id = id;
    this.email = email;
    this.name = name;
    this.hashedPassword = hashedPassword;
    this.role = (role != null) ? role : Role.USER;
    this.isActive = (isActive != null)? isActive : true;
  }

  

}
