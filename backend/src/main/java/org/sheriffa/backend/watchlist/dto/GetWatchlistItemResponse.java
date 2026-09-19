package org.sheriffa.backend.watchlist.dto;

import org.sheriffa.backend.watchlist.WatchlistItem;
import org.sheriffa.backend.watchlist.WatchlistItemMediaType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record GetWatchlistItemResponse(
        UUID id,
        UUID watchlistId,
        String mediaId,
        WatchlistItemMediaType mediaType,
        int position,
        String metadata,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static GetWatchlistItemResponse from(WatchlistItem watchlistItem) {
        return new GetWatchlistItemResponse(
                watchlistItem.getId(),
                watchlistItem.getWatchlistId(),
                watchlistItem.getMediaId(),
                watchlistItem.getMediaType(),
                watchlistItem.getPosition(),
                watchlistItem.getMetadata(),
                watchlistItem.getCreatedAt(),
                watchlistItem.getUpdatedAt()
        );
    }
}
