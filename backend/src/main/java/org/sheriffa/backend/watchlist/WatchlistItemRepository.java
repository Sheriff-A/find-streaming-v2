package org.sheriffa.backend.watchlist;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
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

    public Optional<WatchlistItem> findById(UUID id) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM watchlist_items WHERE id = :id";
        List<WatchlistItem> results = jdbcTemplate.query(sql, new MapSqlParameterSource("id", id), ROW_MAPPER);
        return results.stream().findFirst();
    }

    public WatchlistItem insert(WatchlistItem watchlistItem) {
        String sql = """
                INSERT INTO watchlist_items (id, watchlist_id, media_id, media_type, position, metadata)
                VALUES (COALESCE(:id, gen_random_uuid()), :watchlistId, :mediaId, :mediaType, :position, :metadata)
                RETURNING %s
                """.formatted(SELECT_COLUMNS);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", watchlistItem.getId())
                .addValue("watchlistId", watchlistItem.getWatchlistId())
                .addValue("mediaId", watchlistItem.getMediaId())
                .addValue("mediaType", watchlistItem.getMediaType().name())
                .addValue("position", watchlistItem.getPosition())
                .addValue("metadata", watchlistItem.getMetadata(), Types.OTHER);
        return jdbcTemplate.queryForObject(sql, params, ROW_MAPPER);
    }

    public Optional<WatchlistItem> update(UUID id, WatchlistItem watchlistItem) {
        String sql = """
                UPDATE watchlist_items
                SET media_id = :mediaId, media_type = :mediaType, position = :position, metadata = :metadata, updated_at = now()
                WHERE id = :id
                RETURNING %s
                """.formatted(SELECT_COLUMNS);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("mediaId", watchlistItem.getMediaId())
                .addValue("mediaType", watchlistItem.getMediaType().name())
                .addValue("position", watchlistItem.getPosition())
                .addValue("metadata", watchlistItem.getMetadata(), Types.OTHER);
        List<WatchlistItem> results = jdbcTemplate.query(sql, params, ROW_MAPPER);
        return results.stream().findFirst();
    }

    public void deleteById(UUID id) {
        String sql = "DELETE FROM watchlist_items WHERE id = :id";
        jdbcTemplate.update(sql, new MapSqlParameterSource("id", id));
    }

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
