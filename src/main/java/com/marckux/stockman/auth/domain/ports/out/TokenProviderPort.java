package com.marckux.stockman.auth.domain.ports.out;

import com.marckux.stockman.auth.domain.model.User;

public interface TokenProviderPort {

  String generateToken(User user);
}
