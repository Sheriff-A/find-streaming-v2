package org.sheriffa.backend.watchlist;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class WatchlistItemRepository {
    private final int POSITION_INCREMENT = 1000;

    private static final RowMapper<WatchlistItem> ROW_MAPPER = (rs, rowNum) -> new WatchlistItem(
            rs.getObject("id", UUID.class),
            rs.getObject("watchlist_id", UUID.class),
            rs.getString("media_id"),
            WatchlistItemMediaType.valueOf(rs.getString("media_type")),
            rs.getInt("position"),
            rs.getString("metadata"),
            rs.getObject("created_at", OffsetDateTime.class),
            rs.getObject("updated_at", OffsetDateTime.class)
    );

    private final String SELECT_COLUMNS = "id, watchlist_id, media_id, media_type, position, metadata, created_at, updated_at";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public WatchlistItemRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<WatchlistItem> findAllByWatchlistId(UUID watchlistId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM watchlist_items WHERE watchlist_id = :watchlistId ORDER BY position";
        return jdbcTemplate.query(sql, new MapSqlParameterSource("watchlistId", watchlistId), ROW_MAPPER);
    }

    public WatchlistItem insert(WatchlistItem watchlistItem) {
        String sql = """
                INSERT INTO watchlist_items (watchlist_id, media_id, media_type, position, metadata)
                VALUES (:watchlistId, :mediaId, :mediaType, :position, :metadata)
                RETURNING %s
                """.formatted(SELECT_COLUMNS);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("watchlistId", watchlistItem.getWatchlistId())
                .addValue("mediaId", watchlistItem.getMediaId())
                .addValue("mediaType", watchlistItem.getMediaType().name())
                .addValue("position", watchlistItem.getPosition())
                .addValue("metadata", watchlistItem.getMetadata(), Types.OTHER);
        return jdbcTemplate.queryForObject(sql, params, ROW_MAPPER);
    }

    // TODO: Implement update and delete watchlist items

    // Get the next position for the new watchlistItem in the watchlist
    public int nextPositionFor(UUID watchlistId) {
        String sql = "SELECT COALESCE(MAX(position), 0) + " + POSITION_INCREMENT + " FROM watchlist_items WHERE watchlist_id = :watchlistId";
        Integer next = jdbcTemplate.queryForObject(
                sql,
                new MapSqlParameterSource("watchlistId", watchlistId),
                Integer.class
        );
        return next == null ? POSITION_INCREMENT : next;
    }
}
