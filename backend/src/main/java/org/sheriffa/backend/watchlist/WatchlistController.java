package org.sheriffa.backend.watchlist;

import jakarta.validation.Valid;
import org.sheriffa.backend.watchlist.dto.CreateWatchlistRequest;
import org.sheriffa.backend.watchlist.dto.GetWatchlistResponse;
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
    public void updateWatchlist(@PathVariable UUID watchlistId) {
    }

    @PostMapping("/")
    public ResponseEntity<GetWatchlistResponse> createWatchlist(@Valid @RequestBody CreateWatchlistRequest request) {
        Watchlist watchlist = watchlistService.createWatchlist(CURRENT_OWNER_ID, request.name(), request.description(), request.visibility());
        URI location = URI.create("/api/v1/watchlist/" + watchlist.getId());
        return ResponseEntity.created(location).body(GetWatchlistResponse.from(watchlist));

    }

    @DeleteMapping("/{watchlistId}")
    public void deleteWatchlist(@PathVariable UUID watchlistId) {
    }

    @GetMapping("/{watchlistId}/media")
    public void getWatchlistMedia(@PathVariable UUID watchlistId) {
    }

    @PutMapping("/{watchlistId}/media")
    public void updateWatchlistMedia(@PathVariable UUID watchlistId) {
    }

    @PostMapping("/{watchlistId}/media")
    public void addWatchlistMedia(@PathVariable UUID watchlistId) {
    }

    @DeleteMapping("/{watchlistId}/media")
    public void removeWatchlistMedia(@PathVariable UUID watchlistId) {
    }
}
