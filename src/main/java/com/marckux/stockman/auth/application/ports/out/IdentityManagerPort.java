package com.marckux.stockman.auth.application.ports.out;

public interface IdentityManagerPort {

  void authenticate(String email, String password);
}
