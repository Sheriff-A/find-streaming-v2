package org.sheriffa.backend.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;
    private final SecureRandom secureRandom;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtProperties jwtProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProperties = jwtProperties;
        this.secureRandom = new SecureRandom();
    }

    public String issue(UUID userId) {
        String rawToken = generateRawToken();
        RefreshToken toInsert = new RefreshToken(
                null,
                userId,
                hash(rawToken),
                OffsetDateTime.now().plusDays(
                        this.jwtProperties.refreshExpirationDays()
                ),
                null,
                null
        );
        refreshTokenRepository.insert(toInsert);
        return rawToken;
    }

    public String rotate(String rawToken) {
        RefreshToken existing =
                refreshTokenRepository.findByTokenHash(hash(rawToken))
                        .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        if (existing.getRevokedAt() != null || existing.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new BadCredentialsException("Refresh token has expired");
        }

        refreshTokenRepository.revoke(existing.getId());
        return issue(existing.getUserId());
    }

    public void revoke(String rawToken) {
        refreshTokenRepository.findByTokenHash(hash(rawToken))
                .ifPresent(token -> refreshTokenRepository.revoke(token.getId()));
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        this.secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String rawToken) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = messageDigest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not supported", e);
        }
    }

}
