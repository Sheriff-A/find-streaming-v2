package org.sheriffa.backend.watchlist;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;

    public WatchlistService(WatchlistRepository watchlistRepository) {
        this.watchlistRepository = watchlistRepository;
    }

    public List<Watchlist> getWatchlistForOwner(UUID ownerId) {
        return watchlistRepository.findAllByOwnerId(ownerId);
    }

    public Watchlist getWatchlistById(UUID id, UUID requesterId) {
        Watchlist watchlist = watchlistRepository.findById(id)
                .orElseThrow(() -> new WatchlistNotFoundException(id));

        boolean isOwner = watchlist.getOwnerId().equals(requesterId);
        if (watchlist.getVisibility() == WatchlistVisibility.PRIVATE && !isOwner) {
            // A non-owner asking for a private watchlist
            // Returns as if nonexistent
            // Don't want to leak "this exists but isn't yours".
            throw new WatchlistNotFoundException(id);
        }
        return watchlist;
    }

    public Watchlist createWatchlist(UUID ownerId, String name, String description, WatchlistVisibility visibility) {
        Watchlist toCreate = new Watchlist(null, ownerId, name, description, visibility, null, null);
        return watchlistRepository.insert(toCreate);
    }

    // TODO: updateWatchlist(UUID id, UUID requesterId, String name, String description, WatchlistVisibility visibility)
    //   1. watchlistRepository.findById(id), else throw WatchlistNotFoundException - same as getWatchlistById.
    //   2. if requesterId doesn't match watchlist.getOwnerId(), throw WatchlistForbiddenException.
    //      (Forbidden is fine here, unlike the read path above - the caller already knows this id
    //      exists since they're the one trying to act on it, so there's nothing left to hide.)
    //   3. watchlistRepository.update(id, name, description, visibility) and return the result
    //      (it returns Optional<Watchlist> - empty would mean the row vanished between steps 1
    //      and 3, which .orElseThrow(() -> new WatchlistNotFoundException(id)) handles cleanly).

    // TODO: deleteWatchlist(UUID id, UUID requesterId)
    //   Same ownership check as updateWatchlist (steps 1-2), then watchlistRepository.deleteById(id).
}
