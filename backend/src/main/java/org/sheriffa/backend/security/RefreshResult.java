package org.sheriffa.backend.security;

import java.util.UUID;

public record RefreshResult(UUID userId, String refreshToken) {
}
