package org.sheriffa.backend.watchlist.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.sheriffa.backend.watchlist.WatchlistItemMediaType;

public record CreateWatchlistItemRequest(
        @NotBlank @Size(max = 255) String mediaId,
        @NotNull WatchlistItemMediaType mediaType,
        @NotNull String metadata
) {
}
