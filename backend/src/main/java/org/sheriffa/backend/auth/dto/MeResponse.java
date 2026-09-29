package org.sheriffa.backend.auth.dto;

import org.sheriffa.backend.security.AuthenticatedUser;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.UUID;

public record MeResponse(
        UUID id,
        String email,
        List<String> roles
) {
    public static MeResponse from(AuthenticatedUser user) {
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return new MeResponse(user.getId(), user.getUsername(), roles);
    }
}
