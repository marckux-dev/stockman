package com.marckux.stockman.auth.domain.ports.out;

import com.marckux.stockman.auth.domain.model.User;

public interface AccountNotificationPort {

  void sendActivationToken(User user, String token);

  void sendPasswordResetToken(User user, String token);
}
