package org.sheriffa.backend.watchlist;

import org.sheriffa.backend.common.NotFoundException;

import java.util.UUID;

public class WatchlistItemNotFoundException extends NotFoundException {
    public WatchlistItemNotFoundException(UUID id) {
        super("Watchlist item not found: " + id);
    }
}
