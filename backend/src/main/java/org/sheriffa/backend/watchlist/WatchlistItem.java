package org.sheriffa.backend.watchlist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchlistItem {
    private UUID id;
    private UUID watchlistId;
    private String mediaId;
    private WatchlistItemMediaType mediaType;
    private int position;
    private String metadata; // TODO: Type this with what data I need to store so the item can be displayed properly
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
