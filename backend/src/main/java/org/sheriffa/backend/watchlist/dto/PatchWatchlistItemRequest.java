package org.sheriffa.backend.watchlist.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.sheriffa.backend.common.NullOrNotBlank;
import org.sheriffa.backend.watchlist.WatchlistItemMediaType;

public record PatchWatchlistItemRequest(
        @NullOrNotBlank @Size(max = 255) String mediaId,
        WatchlistItemMediaType mediaType,
        @NullOrNotBlank String metadata,
        @Min(0) Integer position
) {
}
