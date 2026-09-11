package org.sheriffa.backend.watchlist;

import org.sheriffa.backend.common.ForbiddenException;

import java.util.UUID;

public class WatchlistForbiddenException extends ForbiddenException {
    public WatchlistForbiddenException(UUID id) {
        super("Not permitted to modify watchlist: " + id);
    }
}
