package org.sheriffa.backend.watchlist;

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
    private Watchlist optionalGetWatchList(UUID watchlistId) {
        return watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new WatchlistNotFoundException(watchlistId));
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

    public Watchlist updateWatchlist(UUID watchlistId, UUID requesterId, String name, String description, WatchlistVisibility visibility) {
        Watchlist watchlist = optionalGetWatchList(watchlistId);

        if (!isWatchlistOwner(watchlist, requesterId)) {
            // A non-owner asking to update a watchlist
            // Forbidden.
            // Cannot update a watchlist that's not yours.'
            throw new WatchlistForbiddenException(watchlistId);
        }
        Watchlist toUpdate = new Watchlist(watchlistId, requesterId, name, description, visibility, null, null);
        return watchlistRepository.update(watchlistId, toUpdate).orElseThrow(() -> new WatchlistNotFoundException(watchlistId));
    }

    public void deleteWatchlist(UUID watchlistId, UUID requesterId) {
        Watchlist watchlist = optionalGetWatchList(watchlistId);

        if (!isWatchlistOwner(watchlist, requesterId)) {
            // A non-owner asking to delete a watchlist
            // Forbidden.
            // Cannot delete a watchlist that's not yours.'
            throw new WatchlistForbiddenException(watchlistId);
        }
        watchlistRepository.deleteById(watchlistId);
    }

    public Watchlist patchWatchlist(UUID watchlistId, UUID requesterId, String name, String description, WatchlistVisibility visibility) {
        Watchlist watchlist = optionalGetWatchList(watchlistId);

        if (!isWatchlistOwner(watchlist, requesterId)) {
            // A non-owner asking to patch a watchlist
            // Forbidden.
            // Cannot patch a watchlist that's not yours.'
            throw new WatchlistForbiddenException(watchlistId);
        }

        String updatedName = name != null ? name : watchlist.getName();
        String updatedDescription = description != null ? description : watchlist.getDescription();
        WatchlistVisibility updatedVisibility = visibility != null ? visibility : watchlist.getVisibility();

        Watchlist toPatch = new Watchlist(watchlistId, requesterId, updatedName, updatedDescription, updatedVisibility, null, null);
        return watchlistRepository.update(watchlistId, toPatch).orElseThrow(() -> new WatchlistNotFoundException(watchlistId));
    }

    public List<WatchlistItem> getWatchlistItems(UUID watchlistId, UUID requesterId) {
        Watchlist watchlist = optionalGetWatchList(watchlistId);

        if (!isWatchlistOwner(watchlist, requesterId)) {
            // A non-owner asking to get items in a watchlist
            // Forbidden.
            // Cannot get items in a watchlist that's not yours.'
            throw new WatchlistForbiddenException(watchlistId);
        }

        return watchlistItemRepository.findAllByWatchlistId(watchlistId);

    }

    public WatchlistItem createWatchlistItem(UUID watchlistId, UUID requesterId, String mediaId, WatchlistItemMediaType mediaType, String metadata) {
        Watchlist watchlist = optionalGetWatchList(watchlistId);

        if (!isWatchlistOwner(watchlist, requesterId)) {
            // A non-owner asking to create an item in a watchlist
            // Forbidden.
            // Cannot create an item in a watchlist that's not yours.'
            throw new WatchlistForbiddenException(watchlistId);
        }

        int position = watchlistItemRepository.nextPositionFor(watchlistId);
        WatchlistItem toCreate = new WatchlistItem(null, watchlistId, mediaId, mediaType, position, metadata, null, null);
        return watchlistItemRepository.insert(toCreate);
    }
}
