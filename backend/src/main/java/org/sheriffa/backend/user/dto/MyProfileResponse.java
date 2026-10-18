package org.sheriffa.backend.user.dto;

import org.sheriffa.backend.user.UserStatus;
import tools.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record MyProfileResponse(
        UUID id,
        String email,
        List<String> roles,
        UserStatus status,
        String displayName,
        String avatarUrl,
        String bio,
        JsonNode preferences,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
