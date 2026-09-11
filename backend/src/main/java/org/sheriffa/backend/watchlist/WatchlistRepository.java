package org.sheriffa.backend.watchlist;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class WatchlistRepository {

    // This repository and corresponding models use JDBC
    // The goal is to contrast the use of JPA with the use of JDBC
    private static final RowMapper<Watchlist> ROW_MAPPER = (rs, rowNum) -> new Watchlist(
            rs.getObject("id", UUID.class),
            rs.getObject("owner_id", UUID.class),
            rs.getString("name"),
            rs.getString("description"),
            WatchlistVisibility.valueOf(rs.getString("visibility")),
            rs.getObject("created_at", OffsetDateTime.class),
            rs.getObject("updated_at", OffsetDateTime.class)
    );

    private static final String SELECT_COLUMNS =
            "id, owner_id, name, description, visibility, created_at, updated_at";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public WatchlistRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Watchlist> findAllByOwnerId(UUID ownerId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM watchlist WHERE owner_id = :ownerId ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new MapSqlParameterSource("ownerId", ownerId), ROW_MAPPER);
    }

    public Optional<Watchlist> findById(UUID id) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM watchlist WHERE id = :id";
        List<Watchlist> results = jdbcTemplate.query(sql, new MapSqlParameterSource("id", id), ROW_MAPPER);
        return results.stream().findFirst();
    }

    public Watchlist insert(Watchlist watchlist) {
        String sql = """
                INSERT INTO watchlist (owner_id, name, description, visibility)
                VALUES (:ownerId, :name, :description, :visibility)
                RETURNING %s
                """.formatted(SELECT_COLUMNS);
        var params = new MapSqlParameterSource()
                .addValue("ownerId", watchlist.getOwnerId())
                .addValue("name", watchlist.getName())
                .addValue("description", watchlist.getDescription())
                .addValue("visibility", watchlist.getVisibility().name());
        return jdbcTemplate.queryForObject(sql, params, ROW_MAPPER);
    }

    public Optional<Watchlist> update(UUID id, String name, String description, WatchlistVisibility visibility) {
        String sql = """
                UPDATE watchlist
                SET name = :name, description = :description, visibility = :visibility, updated_at = now()
                WHERE id = :id
                RETURNING %s
                """.formatted(SELECT_COLUMNS);
        var params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("name", name)
                .addValue("description", description)
                .addValue("visibility", visibility.name());
        List<Watchlist> results = jdbcTemplate.query(sql, params, ROW_MAPPER);
        return results.stream().findFirst();
    }

    public boolean deleteById(UUID id) {
        String sql = "DELETE FROM watchlist WHERE id = :id";
        return jdbcTemplate.update(sql, new MapSqlParameterSource("id", id)) > 0;
    }
}
