package org.sheriffa.backend.watchlist;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Watchlist {
    private UUID id;
    private UUID ownerId;
    private String name;
    private String description;
    private WatchlistVisibility visibility;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
