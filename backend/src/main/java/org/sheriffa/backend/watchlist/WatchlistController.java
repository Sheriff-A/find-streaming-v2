package org.sheriffa.backend.watchlist;

import org.sheriffa.backend.watchlist.dto.GetWatchlistResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public void getWatchlistById(@PathVariable String watchlistId) {
    }

    @PutMapping("/{watchlistId}")
    public void updateWatchlist(@PathVariable String watchlistId) {
    }

    @PostMapping("/")
    public void createWatchlist() {
    }

    @DeleteMapping("/{watchlistId}")
    public void deleteWatchlist(@PathVariable String watchlistId) {
    }

    @GetMapping("/{watchlistId}/media")
    public void getWatchlistMedia(@PathVariable String watchlistId) {
    }

    @PutMapping("/{watchlistId}/media")
    public void updateWatchlistMedia(@PathVariable String watchlistId) {
    }

    @PostMapping("/{watchlistId}/media")
    public void addWatchlistMedia(@PathVariable String watchlistId) {
    }

    @DeleteMapping("/{watchlistId}/media")
    public void removeWatchlistMedia(@PathVariable String watchlistId) {
    }
}
