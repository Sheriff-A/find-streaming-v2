package org.sheriffa.backend.security;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RefreshTokenRepository {

    private static final RowMapper<RefreshToken> ROW_MAPPER = (rs, rowNum) -> new RefreshToken(
            rs.getObject("id", UUID.class),
            rs.getObject("user_id", UUID.class),
            rs.getString("token_hash"),
            rs.getObject("expires_at", OffsetDateTime.class),
            rs.getObject("revoked_at", OffsetDateTime.class),
            rs.getObject("created_at", OffsetDateTime.class)
    );

    private static final String SELECT_COLUMNS =
            "id, user_id, token_hash, expires_at, revoked_at, created_at";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public RefreshTokenRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public RefreshToken insert(RefreshToken refreshToken) {
        String sql = """
                INSERT INTO refresh_tokens (id, user_id, token_hash, expires_at)
                VALUES (COALESCE(:id, gen_random_uuid()), :userId, :tokenHash, :expiresAt)
                RETURNING %s
                """.formatted(SELECT_COLUMNS);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", refreshToken.getId())
                .addValue("userId", refreshToken.getUserId())
                .addValue("tokenHash", refreshToken.getTokenHash())
                .addValue("expiresAt", refreshToken.getExpiresAt());
        return jdbcTemplate.queryForObject(sql, params, ROW_MAPPER);
    }

    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM refresh_tokens WHERE token_hash = :tokenHash";
        MapSqlParameterSource params = new MapSqlParameterSource("tokenHash", tokenHash);
        List<RefreshToken> results = jdbcTemplate.query(sql, params, ROW_MAPPER);
        return results.stream().findFirst();
    }

    public void revoke(UUID id) {
        String sql = "UPDATE refresh_tokens SET revoked_at = now() WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", id);
        jdbcTemplate.update(sql, params);
    }
}
