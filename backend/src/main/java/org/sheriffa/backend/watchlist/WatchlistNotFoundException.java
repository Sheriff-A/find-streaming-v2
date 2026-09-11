package org.sheriffa.backend.watchlist;

import org.sheriffa.backend.common.NotFoundException;

import java.util.UUID;

public class WatchlistNotFoundException extends NotFoundException {
    public WatchlistNotFoundException(UUID id) {
        super("Watchlist not found: " + id);
    }
}
