package org.sheriffa.backend.user.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import org.sheriffa.backend.common.NullOrNotBlank;
import tools.jackson.databind.JsonNode;

public record PatchProfileRequest(
        @NullOrNotBlank @Size(max = 255) String displayName,
        @Size(max = 255) String avatarUrl,
        @Size(max = 255) String bio,
        JsonNode preferences
) {
    @AssertTrue(message = "Preferences must be a valid JSON Object")
    public boolean isPreferencesValid() {
        return preferences == null || preferences.isObject();
    }
}
