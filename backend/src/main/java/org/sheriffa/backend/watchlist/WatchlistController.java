package org.sheriffa.backend.watchlist;

import jakarta.validation.Valid;
import org.sheriffa.backend.common.PutResponseState;
import org.sheriffa.backend.watchlist.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/watchlist")
public class WatchlistController {

    // TODO:
    // Seeded user, Alice, standing in for real auth
    // Real auth will resolve the called from the request
    // Every method below that needs "who's asking" should take it from the request
    private static final UUID CURRENT_OWNER_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping("/")
    public ResponseEntity<List<GetWatchlistResponse>> getAllWatchlist() {
        List<GetWatchlistResponse> response =
                watchlistService
                        .getWatchlistForOwner(CURRENT_OWNER_ID)
                        .stream()
                        .map((wl) ->
                                GetWatchlistResponse.from(wl.watchlist(), wl.itemCount())
                        )
                        .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{watchlistId}")
    public ResponseEntity<GetWatchlistResponse> getWatchlistById(@PathVariable UUID watchlistId) {
        WatchlistWithItemCount response = watchlistService.getWatchlistById(watchlistId, CURRENT_OWNER_ID);
        return ResponseEntity.ok(
                GetWatchlistResponse.from(
                        response.watchlist(), response.itemCount()
                )
        );
    }

    @PutMapping("/{watchlistId}")
    public ResponseEntity<GetWatchlistResponse> updateWatchlist(@PathVariable UUID watchlistId, @Valid @RequestBody UpdateWatchlistRequest request) {
        PutResponseState<WatchlistWithItemCount> response = watchlistService.upsertWatchlist(
                watchlistId,
                CURRENT_OWNER_ID,
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
    public ResponseEntity<GetWatchlistResponse> patchWatchlist(@PathVariable UUID watchlistId, @Valid @RequestBody PatchWatchlistRequest request) {
        WatchlistWithItemCount response = watchlistService.patchWatchlist(
                watchlistId,
                CURRENT_OWNER_ID,
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
    public ResponseEntity<GetWatchlistResponse> createWatchlist(@Valid @RequestBody CreateWatchlistRequest request) {
        WatchlistWithItemCount watchlistWithItemCount = watchlistService.createWatchlist(
                null,
                CURRENT_OWNER_ID,
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
    public ResponseEntity<Void> deleteWatchlist(@PathVariable UUID watchlistId) {
        watchlistService.deleteWatchlist(watchlistId, CURRENT_OWNER_ID);
        return ResponseEntity.noContent().build();
    }

    // Get Watchlist Items in the Watchlist with ID: {{ watchlistId }}
    @GetMapping("/{watchlistId}/media")
    public ResponseEntity<List<GetWatchlistItemResponse>> getWatchlistMedia(@PathVariable UUID watchlistId) {
        List<GetWatchlistItemResponse> response =
                watchlistService
                        .getWatchlistItems(watchlistId, CURRENT_OWNER_ID)
                        .stream()
                        .map(GetWatchlistItemResponse::from)
                        .toList();
        return ResponseEntity.ok(response);
    }

    // Update Watchlist Item with ID: {{ watchlistItemId}}, in the Watchlist with ID: {{ watchlistId }}
    @PutMapping("/{watchlistId}/media/{watchlistItemId}")
    public ResponseEntity<GetWatchlistItemResponse> updateWatchlistMedia(@PathVariable UUID watchlistId, @PathVariable UUID watchlistItemId, @Valid @RequestBody UpdateWatchlistItemRequest request) {
        PutResponseState<WatchlistItem> response = watchlistService.upsertWatchlistItem(
                watchlistId,
                watchlistItemId,
                CURRENT_OWNER_ID,
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
    public ResponseEntity<GetWatchlistItemResponse> patchWatchlistMedia(@PathVariable UUID watchlistId, @PathVariable UUID watchlistItemId, @Valid @RequestBody PatchWatchlistItemRequest request) {
        WatchlistItem response = watchlistService.patchWatchlistItem(
                watchlistId,
                watchlistItemId,
                CURRENT_OWNER_ID,
                request.mediaId(),
                request.mediaType(),
                request.metadata(),
                request.position()
        );
        return ResponseEntity.ok(GetWatchlistItemResponse.from(response));
    }

    // Add Watchlist Item to the Watchlist with ID: {{ watchlistId }}
    @PostMapping("/{watchlistId}/media")
    public ResponseEntity<GetWatchlistItemResponse> addWatchlistMedia(@PathVariable UUID watchlistId, @Valid @RequestBody CreateWatchlistItemRequest request) {
        WatchlistItem watchlistItem = watchlistService.createWatchlistItem(
                null,
                watchlistId,
                CURRENT_OWNER_ID,
                request.mediaId(),
                request.mediaType(),
                request.metadata()
        );

        URI location = URI.create("/api/v1/watchlist/" + watchlistId + "/media/" + watchlistItem.getId());
        return ResponseEntity.created(location).body(GetWatchlistItemResponse.from(watchlistItem));
    }

    // Remove Watchlist Item with ID: {{ watchlistItemId}}, from the Watchlist with ID: {{ watchlistId }}
    @DeleteMapping("/{watchlistId}/media/{watchlistItemId}")
    public ResponseEntity<Void> removeWatchlistMedia(@PathVariable UUID watchlistId, @PathVariable UUID watchlistItemId) {
        watchlistService.deleteWatchlistItem(watchlistId, watchlistItemId, CURRENT_OWNER_ID);
        return ResponseEntity.noContent().build();
    }
}
