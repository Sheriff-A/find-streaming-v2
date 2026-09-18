package org.sheriffa.backend.watchlist;

import org.sheriffa.backend.common.ForbiddenException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final WatchlistItemRepository watchlistItemRepository;

    public WatchlistService(WatchlistRepository watchlistRepository, WatchlistItemRepository watchlistItemRepository) {
        this.watchlistRepository = watchlistRepository;
        this.watchlistItemRepository = watchlistItemRepository;
    }

    // Check if the watchlist exists, and if not, throw a NotFoundException
    private Watchlist optionalGetWatchList(UUID id) {
        return watchlistRepository.findById(id)
                .orElseThrow(() -> new WatchlistNotFoundException(id));
    }

    private boolean isWatchlistOwner(Watchlist watchlist, UUID requesterId) {
        return watchlist.getOwnerId().equals(requesterId);
    }

    public List<Watchlist> getWatchlistForOwner(UUID ownerId) {
        return watchlistRepository.findAllByOwnerId(ownerId);
    }

    public Watchlist getWatchlistById(UUID id, UUID requesterId) {
        Watchlist watchlist = optionalGetWatchList(id);

        boolean isOwner = isWatchlistOwner(watchlist, requesterId);
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

    public Watchlist updateWatchlist(UUID id, UUID requesterId, String name, String description, WatchlistVisibility visibility) {
        Watchlist watchlist = optionalGetWatchList(id);

        if (!isWatchlistOwner(watchlist, requesterId)) {
            // A non-owner asking to update a watchlist
            // Forbidden.
            // Cannot update a watchlist that's not yours.'
            throw new WatchlistForbiddenException(id);
        }
        Watchlist toUpdate = new Watchlist(id, requesterId, name, description, visibility, null, null);
        return watchlistRepository.update(id, toUpdate).orElseThrow(() -> new WatchlistNotFoundException(id));
    }

    public void deleteWatchlist(UUID id, UUID requesterId) {
        Watchlist watchlist = optionalGetWatchList(id);

        if (!isWatchlistOwner(watchlist, requesterId)) {
            // A non-owner asking to delete a watchlist
            // Forbidden.
            // Cannot delete a watchlist that's not yours.'
            throw new WatchlistForbiddenException(id);
        }
        watchlistRepository.deleteById(id);
    }

    public Watchlist patchWatchlist(UUID id, UUID requesterId, String name, String description, WatchlistVisibility visibility) {
        Watchlist watchlist = optionalGetWatchList(id);

        if (!isWatchlistOwner(watchlist, requesterId)) {
            // A non-owner asking to patch a watchlist
            // Forbidden.
            // Cannot patch a watchlist that's not yours.'
            throw new WatchlistForbiddenException(id);
        }

        String updatedName = name != null ? name : watchlist.getName();
        String updatedDescription = description != null ? description : watchlist.getDescription();
        WatchlistVisibility updatedVisibility = visibility != null ? visibility : watchlist.getVisibility();

        Watchlist toPatch = new Watchlist(id, requesterId, updatedName, updatedDescription, updatedVisibility, null, null);
        return watchlistRepository.update(id, toPatch).orElseThrow(() -> new WatchlistNotFoundException(id));
    }

    public List<WatchlistItem> getWatchlistItemsInWatchlist(UUID id, UUID requesterId) {
        Watchlist watchlist = optionalGetWatchList(id);

        if (!isWatchlistOwner(watchlist, requesterId)) {
            // A non-owner asking to get items in a watchlist
            // Forbidden.
            // Cannot get items in a watchlist that's not yours.'
            throw new WatchlistForbiddenException(id);
        }

        return watchlistItemRepository.findAllByWatchlistId(id);

    }
}
