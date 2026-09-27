-- Superseded by the composite index below: a (watchlist_id, position) index
-- already serves any query that filters on watchlist_id alone (it's the
-- leading column), so keeping both would just be redundant write overhead.
DROP INDEX idx_watchlist_items_watchlist_id;

-- Matches the actual query shape: WHERE watchlist_id = ? ORDER BY position.
CREATE INDEX idx_watchlist_items_watchlist_id_position ON watchlist_items (watchlist_id, position);
