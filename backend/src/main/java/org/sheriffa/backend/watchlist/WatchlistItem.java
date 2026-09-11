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
    private UUID watchlist_id;
    private String media_id;
    private WatchlistItemMediaType media_type;
    private int position;
    private Object metadata;
    private OffsetDateTime created_at;
    private OffsetDateTime updated_at;
}
