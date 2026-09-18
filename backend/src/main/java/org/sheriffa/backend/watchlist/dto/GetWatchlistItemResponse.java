package org.sheriffa.backend.watchlist.dto;

import org.sheriffa.backend.watchlist.WatchlistItem;
import org.sheriffa.backend.watchlist.WatchlistItemMediaType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record GetWatchlistItemResponse(
        UUID id,
        UUID watchlist_id,
        String media_id,
        WatchlistItemMediaType media_type,
        int position,
        String metadata,
        OffsetDateTime created_at,
        OffsetDateTime updated_at
) {
    public static GetWatchlistItemResponse from(WatchlistItem watchlistItem) {
        return new GetWatchlistItemResponse(
                watchlistItem.getId(),
                watchlistItem.getWatchlist_id(),
                watchlistItem.getMedia_id(),
                watchlistItem.getMedia_type(),
                watchlistItem.getPosition(),
                watchlistItem.getMetadata(),
                watchlistItem.getCreated_at(),
                watchlistItem.getUpdated_at()
        );
    }
}
