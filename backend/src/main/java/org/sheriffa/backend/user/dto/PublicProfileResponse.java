package org.sheriffa.backend.user.dto;

import java.util.UUID;

public record PublicProfileResponse(
        UUID id,
        String displayName,
        String avatarUrl,
        String bio
) {
}
