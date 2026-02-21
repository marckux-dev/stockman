package com.marckux.stockman.notification.application.ports.in.usecases;

import com.marckux.stockman.notification.application.dtos.SendRichEmailRequest;

@FunctionalInterface
public interface SendRichEmailUseCase {

  Void execute(SendRichEmailRequest request);
}
