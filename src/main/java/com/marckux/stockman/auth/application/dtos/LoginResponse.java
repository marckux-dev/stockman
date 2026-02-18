package com.marckux.stockman.auth.application.dtos;

public record LoginResponse (
  String token,
  UserResponse user
){}
