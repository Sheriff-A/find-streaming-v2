package org.sheriffa.backend.watchlist;

import jakarta.validation.Valid;
import org.sheriffa.backend.watchlist.dto.CreateWatchlistRequest;
import org.sheriffa.backend.watchlist.dto.GetWatchlistResponse;
import org.sheriffa.backend.watchlist.dto.PatchWatchlistRequest;
import org.sheriffa.backend.watchlist.dto.UpdateWatchlistRequest;
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
        List<GetWatchlistResponse> response = watchlistService.getWatchlistForOwner(CURRENT_OWNER_ID).stream()
                .map(GetWatchlistResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{watchlistId}")
    public ResponseEntity<GetWatchlistResponse> getWatchlistById(@PathVariable UUID watchlistId) {
        Watchlist response = watchlistService.getWatchlistById(watchlistId, CURRENT_OWNER_ID);
        return ResponseEntity.ok(GetWatchlistResponse.from(response));
    }

    @PutMapping("/{watchlistId}")
    public ResponseEntity<GetWatchlistResponse> updateWatchlist(@PathVariable UUID watchlistId, @Valid @RequestBody UpdateWatchlistRequest request) {
        Watchlist response = watchlistService.updateWatchlist(watchlistId, CURRENT_OWNER_ID, request.name(), request.description(), request.visibility());
        return ResponseEntity.ok(GetWatchlistResponse.from(response));
    }

    @PatchMapping("/{watchlistId}")
    public ResponseEntity<GetWatchlistResponse> patchWatchlist(@PathVariable UUID watchlistId, @Valid @RequestBody PatchWatchlistRequest request) {
        Watchlist response = watchlistService.patchWatchlist(watchlistId, CURRENT_OWNER_ID, request.name(), request.description(), request.visibility());
        return ResponseEntity.ok(GetWatchlistResponse.from(response));
    }

    @PostMapping("/")
    public ResponseEntity<GetWatchlistResponse> createWatchlist(@Valid @RequestBody CreateWatchlistRequest request) {
        Watchlist watchlist = watchlistService.createWatchlist(CURRENT_OWNER_ID, request.name(), request.description(), request.visibility());
        URI location = URI.create("/api/v1/watchlist/" + watchlist.getId());
        return ResponseEntity.created(location).body(GetWatchlistResponse.from(watchlist));

    }

    @DeleteMapping("/{watchlistId}")
    public ResponseEntity<Void> deleteWatchlist(@PathVariable UUID watchlistId) {
        watchlistService.deleteWatchlist(watchlistId, CURRENT_OWNER_ID);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{watchlistId}/media")
    public void getWatchlistMedia(@PathVariable UUID watchlistId) {
    }

    @PutMapping("/{watchlistId}/media")
    public void updateWatchlistMedia(@PathVariable UUID watchlistId) {
    }

    @PatchMapping("/{watchlistId}/media")
    public void patchWatchlistMedia(@PathVariable UUID watchlistId) {
    }

    @PostMapping("/{watchlistId}/media")
    public void addWatchlistMedia(@PathVariable UUID watchlistId) {
    }

    @DeleteMapping("/{watchlistId}/media")
    public void removeWatchlistMedia(@PathVariable UUID watchlistId) {
    }
}
