package org.sheriffa.backend.watchlist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.sheriffa.backend.watchlist.WatchlistVisibility;

public record CreateWatchlistRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 255) String description,
        @NotNull WatchlistVisibility visibility
) {
}
