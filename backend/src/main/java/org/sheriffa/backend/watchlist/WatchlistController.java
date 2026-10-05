package org.sheriffa.backend.watchlist;

import jakarta.validation.Valid;
import org.sheriffa.backend.common.PutResponseState;
import org.sheriffa.backend.security.AuthenticatedUser;
import org.sheriffa.backend.watchlist.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping("/")
    public ResponseEntity<List<GetWatchlistResponse>> getAllWatchlist(@AuthenticationPrincipal AuthenticatedUser principal) {
        List<GetWatchlistResponse> response =
                watchlistService
                        .getWatchlistForOwner(principal.getId())
                        .stream()
                        .map((wl) ->
                                GetWatchlistResponse.from(wl.watchlist(), wl.itemCount())
                        )
                        .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{watchlistId}")
    public ResponseEntity<GetWatchlistResponse> getWatchlistById(@PathVariable UUID watchlistId, @AuthenticationPrincipal AuthenticatedUser principal) {
        WatchlistWithItemCount response = watchlistService.getWatchlistById(watchlistId, principal.getId());
        return ResponseEntity.ok(
                GetWatchlistResponse.from(
                        response.watchlist(), response.itemCount()
                )
        );
    }

    @PutMapping("/{watchlistId}")
    public ResponseEntity<GetWatchlistResponse> updateWatchlist(@PathVariable UUID watchlistId, @Valid @RequestBody UpdateWatchlistRequest request, @AuthenticationPrincipal AuthenticatedUser principal) {
        PutResponseState<WatchlistWithItemCount> response = watchlistService.upsertWatchlist(
                watchlistId,
                principal.getId(),
                request.name(),
                request.description(),
                request.visibility()
        );

        if (response.created()) {
            return ResponseEntity.created(URI.create("/api/v1/watchlist/" + watchlistId)).body(
                    GetWatchlistResponse.from(
                            response.entity().watchlist(),
                            response.entity().itemCount()
                    )
            );
        }

        return ResponseEntity.ok(
                GetWatchlistResponse.from(
                        response.entity().watchlist(),
                        response.entity().itemCount()
                )
        );
    }

    @PatchMapping("/{watchlistId}")
    public ResponseEntity<GetWatchlistResponse> patchWatchlist(@PathVariable UUID watchlistId, @Valid @RequestBody PatchWatchlistRequest request, @AuthenticationPrincipal AuthenticatedUser principal) {
        WatchlistWithItemCount response = watchlistService.patchWatchlist(
                watchlistId,
                principal.getId(),
                request.name(),
                request.description(),
                request.visibility()
        );
        return ResponseEntity.ok(
                GetWatchlistResponse.from(
                        response.watchlist(), response.itemCount()
                )
        );
    }

    @PostMapping("/")
    public ResponseEntity<GetWatchlistResponse> createWatchlist(@Valid @RequestBody CreateWatchlistRequest request, @AuthenticationPrincipal AuthenticatedUser principal) {
        WatchlistWithItemCount watchlistWithItemCount = watchlistService.createWatchlist(
                null,
                principal.getId(),
                request.name(),
                request.description(),
                request.visibility()
        );
        Watchlist watchlist = watchlistWithItemCount.watchlist();
        URI location = URI.create("/api/v1/watchlist/" + watchlist.getId());
        return ResponseEntity.created(location).body(
                GetWatchlistResponse.from(
                        watchlist, watchlistWithItemCount.itemCount()
                )
        );

    }

    @DeleteMapping("/{watchlistId}")
    public ResponseEntity<Void> deleteWatchlist(@PathVariable UUID watchlistId, @AuthenticationPrincipal AuthenticatedUser principal) {
        watchlistService.deleteWatchlist(watchlistId, principal.getId());
        return ResponseEntity.noContent().build();
    }

    // Get Watchlist Items in the Watchlist with ID: {{ watchlistId }}
    @GetMapping("/{watchlistId}/media")
    public ResponseEntity<List<GetWatchlistItemResponse>> getWatchlistMedia(@PathVariable UUID watchlistId, @AuthenticationPrincipal AuthenticatedUser principal) {
        List<GetWatchlistItemResponse> response =
                watchlistService
                        .getWatchlistItems(watchlistId, principal.getId())
                        .stream()
                        .map(GetWatchlistItemResponse::from)
                        .toList();
        return ResponseEntity.ok(response);
    }

    // Update Watchlist Item with ID: {{ watchlistItemId}}, in the Watchlist with ID: {{ watchlistId }}
    @PutMapping("/{watchlistId}/media/{watchlistItemId}")
    public ResponseEntity<GetWatchlistItemResponse> updateWatchlistMedia(@PathVariable UUID watchlistId, @PathVariable UUID watchlistItemId, @Valid @RequestBody UpdateWatchlistItemRequest request, @AuthenticationPrincipal AuthenticatedUser principal) {
        PutResponseState<WatchlistItem> response = watchlistService.upsertWatchlistItem(
                watchlistId,
                watchlistItemId,
                principal.getId(),
                request.mediaId(),
                request.mediaType(),
                request.metadata()
        );

        if (response.created()) {
            return ResponseEntity.created(
                    URI.create("/api/v1/watchlist/" + watchlistId + "/media/" + watchlistItemId)
            ).body(
                    GetWatchlistItemResponse.from(
                            response.entity()
                    )
            );
        }

        return ResponseEntity.ok(GetWatchlistItemResponse.from(response.entity()));
    }

    // Patch Watchlist Item with ID: {{ watchlistItemId}}, in the Watchlist with ID: {{ watchlistId }}
    @PatchMapping("/{watchlistId}/media/{watchlistItemId}")
    public ResponseEntity<GetWatchlistItemResponse> patchWatchlistMedia(@PathVariable UUID watchlistId, @PathVariable UUID watchlistItemId, @Valid @RequestBody PatchWatchlistItemRequest request, @AuthenticationPrincipal AuthenticatedUser principal) {
        WatchlistItem response = watchlistService.patchWatchlistItem(
                watchlistId,
                watchlistItemId,
                principal.getId(),
                request.mediaId(),
                request.mediaType(),
                request.metadata(),
                request.position()
        );
        return ResponseEntity.ok(GetWatchlistItemResponse.from(response));
    }

    // Add Watchlist Item to the Watchlist with ID: {{ watchlistId }}
    @PostMapping("/{watchlistId}/media")
    public ResponseEntity<GetWatchlistItemResponse> addWatchlistMedia(@PathVariable UUID watchlistId, @Valid @RequestBody CreateWatchlistItemRequest request, @AuthenticationPrincipal AuthenticatedUser principal) {
        WatchlistItem watchlistItem = watchlistService.createWatchlistItem(
                null,
                watchlistId,
                principal.getId(),
                request.mediaId(),
                request.mediaType(),
                request.metadata()
        );

        URI location = URI.create("/api/v1/watchlist/" + watchlistId + "/media/" + watchlistItem.getId());
        return ResponseEntity.created(location).body(GetWatchlistItemResponse.from(watchlistItem));
    }

    // Remove Watchlist Item with ID: {{ watchlistItemId}}, from the Watchlist with ID: {{ watchlistId }}
    @DeleteMapping("/{watchlistId}/media/{watchlistItemId}")
    public ResponseEntity<Void> removeWatchlistMedia(@PathVariable UUID watchlistId, @PathVariable UUID watchlistItemId, @AuthenticationPrincipal AuthenticatedUser principal) {
        watchlistService.deleteWatchlistItem(watchlistId, watchlistItemId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
