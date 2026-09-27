package org.sheriffa.backend.watchlist;

import jakarta.annotation.Nullable;
import org.sheriffa.backend.common.PutResponseState;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final WatchlistItemRepository watchlistItemRepository;

    public WatchlistService(WatchlistRepository watchlistRepository, WatchlistItemRepository watchlistItemRepository) {
        this.watchlistRepository = watchlistRepository;
        this.watchlistItemRepository = watchlistItemRepository;
    }

    // Check if the watchlist exists
    // Possibly return null or throw a NotFoundException
    // Useful for PUT requests
    // Resource may not exist and needs to be created
    @Nullable
    private Watchlist optionalGetWatchlist(UUID watchlistId, boolean throwIfNotFound) {
        Optional<Watchlist> watchlist = watchlistRepository.findById(watchlistId);
        if (watchlist.isEmpty() && throwIfNotFound) {
            throw new WatchlistNotFoundException(watchlistId);
        }
        return watchlist.orElse(null);
    }

    // Check if the watchlist exists, and if not, throw a NotFoundException
    private Watchlist optionalGetWatchlist(UUID watchlistId) {
        return optionalGetWatchlist(watchlistId, true);
    }

    // Check if the watchlist item exists
    // Possibly return null or throw a NotFoundException
    // Useful for PUT requests
    // Resource may not exist and needs to be created
    @Nullable
    private WatchlistItem optionalGetWatchlistItem(UUID watchlistItemId, boolean throwIfNotFound) {
        Optional<WatchlistItem> watchlistItem = watchlistItemRepository.findById(watchlistItemId);
        if (watchlistItem.isEmpty() && throwIfNotFound) {
            throw new WatchlistItemNotFoundException(watchlistItemId);
        }
        return watchlistItem.orElse(null);
    }

    // Check if the watchlist item exists, and if not, throw a NotFoundException
    private WatchlistItem optionalGetWatchlistItem(UUID watchlistItemId) {
        return optionalGetWatchlistItem(watchlistItemId, true);
    }

    private WatchlistWithItemCount optionalGetWatchlistWithCount(UUID watchlistId) {
        return watchlistRepository.findByIdWithItemCount(watchlistId)
                .orElseThrow(() -> new WatchlistNotFoundException(watchlistId));
    }

    private boolean isWatchlistOwner(Watchlist watchlist, UUID requesterId) {
        return watchlist.getOwnerId().equals(requesterId);
    }

    private boolean isWatchlistOwner(WatchlistWithItemCount watchlist, UUID requesterId) {
        return isWatchlistOwner(watchlist.watchlist(), requesterId);
    }

    private boolean isWatchlistPrivate(WatchlistWithItemCount watchlist) {
        return watchlist.watchlist().getVisibility() == WatchlistVisibility.PRIVATE;
    }

    @Nullable
    private Watchlist getWatchlistWithOwnerAccess(UUID watchlistId, UUID requesterId, boolean throwIfNotFound) {
        Watchlist watchlist = optionalGetWatchlist(watchlistId, throwIfNotFound);

        if (watchlist == null) {
            return null;
        }

        if (!isWatchlistOwner(watchlist, requesterId)) {
            // A non-owner asking for a watchlist
            // Forbidden.
            // Cannot access a watchlist that's not yours.'
            throw new WatchlistForbiddenException(watchlistId);
        }

        return watchlist;
    }

    private Watchlist getWatchlistWithOwnerAccess(UUID watchlistId, UUID requesterId) {
        return getWatchlistWithOwnerAccess(watchlistId, requesterId, true);
    }

    public List<WatchlistWithItemCount> getWatchlistForOwner(UUID ownerId) {
        return watchlistRepository.findAllWithItemCountByOwnerId(ownerId);
    }

    public WatchlistWithItemCount getWatchlistById(UUID watchlistId, UUID requesterId) {
        WatchlistWithItemCount watchlist = optionalGetWatchlistWithCount(watchlistId);

        boolean isOwner = isWatchlistOwner(watchlist, requesterId);
        if (isWatchlistPrivate(watchlist) && !isOwner) {
            // A non-owner asking for a private watchlist
            // Returns as if nonexistent
            // Don't want to leak "this exists but isn't yours".
            throw new WatchlistNotFoundException(watchlistId);
        }
        return watchlist;
    }

    public WatchlistWithItemCount createWatchlist(@Nullable UUID id, UUID ownerId, String name, String description, WatchlistVisibility visibility) {
        Watchlist toCreate = new Watchlist(id, ownerId, name, description, visibility, null, null);
        Watchlist created = watchlistRepository.insert(toCreate);
        return watchlistRepository.findByIdWithItemCount(created.getId()).orElseThrow(() -> new WatchlistNotFoundException(created.getId()));
    }

    public PutResponseState<WatchlistWithItemCount> upsertWatchlist(UUID watchlistId, UUID requesterId, String name, String description, WatchlistVisibility visibility) {
        Watchlist existingWatchlist = getWatchlistWithOwnerAccess(watchlistId, requesterId, false);
        if (existingWatchlist == null) {
            // Watchlist does not exist
            // This is a put request
            // Need to create it
            WatchlistWithItemCount created = createWatchlist(watchlistId, requesterId, name, description, visibility);
            return new PutResponseState<>(created, true);
        }

        Watchlist toUpdate = new Watchlist(watchlistId, requesterId, name, description, visibility, null, null);
        watchlistRepository.update(watchlistId, toUpdate).orElseThrow(() -> new WatchlistNotFoundException(watchlistId));
        // Update call cannot do joins and return the count of items,
        // So we have to refetch the watchlist
        WatchlistWithItemCount updatedWatchlist = watchlistRepository.findByIdWithItemCount(watchlistId).orElseThrow(() -> new WatchlistNotFoundException(watchlistId));
        return new PutResponseState<>(updatedWatchlist, false);
    }

    public void deleteWatchlist(UUID watchlistId, UUID requesterId) {
        getWatchlistWithOwnerAccess(watchlistId, requesterId);
        watchlistRepository.deleteById(watchlistId);
    }

    public WatchlistWithItemCount patchWatchlist(UUID watchlistId, UUID requesterId, String name, String description, WatchlistVisibility visibility) {
        Watchlist watchlist = getWatchlistWithOwnerAccess(watchlistId, requesterId);

        String updatedName = name != null ? name : watchlist.getName();
        String updatedDescription = description != null ? description : watchlist.getDescription();
        WatchlistVisibility updatedVisibility = visibility != null ? visibility : watchlist.getVisibility();

        Watchlist toPatch = new Watchlist(watchlistId, requesterId, updatedName, updatedDescription, updatedVisibility, null, null);
        watchlistRepository.update(watchlistId, toPatch).orElseThrow(() -> new WatchlistNotFoundException(watchlistId));
        // Update call cannot do joins and return the count of items,
        // So we have to refetch the watchlist
        return watchlistRepository.findByIdWithItemCount(watchlistId).orElseThrow(() -> new WatchlistNotFoundException(watchlistId));
    }

    public List<WatchlistItem> getWatchlistItems(UUID watchlistId, UUID requesterId) {
        getWatchlistWithOwnerAccess(watchlistId, requesterId);

        return watchlistItemRepository.findAllByWatchlistId(watchlistId);

    }

    public WatchlistItem createWatchlistItem(@Nullable UUID watchlistItemId, UUID watchlistId, UUID requesterId, String mediaId, WatchlistItemMediaType mediaType, String metadata) {
        getWatchlistWithOwnerAccess(watchlistId, requesterId);

        int position = watchlistItemRepository.nextPositionFor(watchlistId);
        WatchlistItem toCreate = new WatchlistItem(watchlistItemId, watchlistId, mediaId, mediaType, position, metadata, null, null);
        return watchlistItemRepository.insert(toCreate);
    }

    public PutResponseState<WatchlistItem> upsertWatchlistItem(UUID watchlistId, UUID watchlistItemId, UUID requesterId, String mediaId, WatchlistItemMediaType mediaType, String metadata) {
        getWatchlistWithOwnerAccess(watchlistId, requesterId);
        WatchlistItem existingWatchlistItem = optionalGetWatchlistItem(watchlistItemId, false);
        if (existingWatchlistItem == null) {
            // Watchlist item does not exist
            // This is a put request
            // Need to create it
            WatchlistItem created = createWatchlistItem(watchlistItemId, watchlistId, requesterId, mediaId, mediaType, metadata);
            return new PutResponseState<>(created, true);
        }

        WatchlistItem toUpdate = new WatchlistItem(watchlistItemId, watchlistId, mediaId, mediaType, existingWatchlistItem.getPosition(), metadata, null, null);

        WatchlistItem updated = watchlistItemRepository.update(watchlistItemId, toUpdate).orElseThrow(() -> new WatchlistItemNotFoundException(watchlistItemId));
        return new PutResponseState<>(updated, false);
    }

    public void deleteWatchlistItem(UUID watchlistId, UUID watchlistItemId, UUID requesterId) {
        getWatchlistWithOwnerAccess(watchlistId, requesterId);
        optionalGetWatchlistItem(watchlistItemId);

        watchlistItemRepository.deleteById(watchlistItemId);
    }

    public WatchlistItem patchWatchlistItem(UUID watchlistId, UUID watchlistItemId, UUID requesterId, String mediaId, WatchlistItemMediaType mediaType, String metadata, Integer position) {
        getWatchlistWithOwnerAccess(watchlistId, requesterId);
        WatchlistItem watchlistItem = optionalGetWatchlistItem(watchlistItemId);

        String updatedMediaId = mediaId != null ? mediaId : watchlistItem.getMediaId();
        WatchlistItemMediaType updatedMediaType = mediaType != null ? mediaType : watchlistItem.getMediaType();
        String updatedMetadata = metadata != null ? metadata : watchlistItem.getMetadata();
        int updatedPosition = position != null ? position : watchlistItem.getPosition();

        WatchlistItem toPatch = new WatchlistItem(watchlistItemId, watchlistId, updatedMediaId, updatedMediaType, updatedPosition, updatedMetadata, null, null);
        return watchlistItemRepository.update(watchlistItemId, toPatch).orElseThrow(() -> new WatchlistItemNotFoundException(watchlistItemId));
    }
}
