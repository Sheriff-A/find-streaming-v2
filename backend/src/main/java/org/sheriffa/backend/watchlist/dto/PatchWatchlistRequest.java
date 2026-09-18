package org.sheriffa.backend.watchlist.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.sheriffa.backend.common.NullOrNotBlank;
import org.sheriffa.backend.watchlist.WatchlistVisibility;

public record PatchWatchlistRequest(
        @NullOrNotBlank @Size(max = 255) String name,
        @Size(max = 255) String description,
        WatchlistVisibility visibility
) {
    // Assertion on the record to validate custom constraints
    // @NotBlank is not enough because it violates PATCH semantics
    // Description is optional, but if it is provided, it cannot be blank

    // Alternative approach to custom Bean Validation (see name)
    // This is best when there is not much logic to reuse
    @AssertTrue(message = "Description cannot be blank")
    public boolean isDescriptionValid() {
        return description == null || !description.isBlank();
    }
}
