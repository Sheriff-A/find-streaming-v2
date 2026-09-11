package org.sheriffa.backend.watchlist.dto;

import org.sheriffa.backend.watchlist.Watchlist;
import org.sheriffa.backend.watchlist.WatchlistVisibility;

import java.time.OffsetDateTime;
import java.util.UUID;

public record GetWatchlistResponse(
        UUID id,
        UUID ownerId,
        String name,
        String description,
        WatchlistVisibility visibility,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static GetWatchlistResponse from(Watchlist watchlist) {
        return new GetWatchlistResponse(
                watchlist.getId(),
                watchlist.getOwnerId(),
                watchlist.getName(),
                watchlist.getDescription(),
                watchlist.getVisibility(),
                watchlist.getCreatedAt(),
                watchlist.getUpdatedAt()
        );
    }
}
