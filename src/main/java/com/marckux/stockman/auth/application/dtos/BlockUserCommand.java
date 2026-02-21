package com.marckux.stockman.auth.application.dtos;

import java.util.UUID;

public record BlockUserCommand(
    UUID targetUserId,
    String requesterEmail
) {
}
