package org.sheriffa.backend.auth;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.sheriffa.backend.auth.dto.AuthResponse;
import org.sheriffa.backend.security.*;
import org.sheriffa.backend.user.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final RefreshTokenService refreshTokenService;
    private final AppUserDetailsService appUserDetailsService;

    public AuthService(
            UserRepository userRepository,
            ProfileRepository profileRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            JwtProperties jwtProperties,
            RefreshTokenService refreshTokenService,
            AppUserDetailsService appUserDetailsService
    ) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.refreshTokenService = refreshTokenService;
        this.appUserDetailsService = appUserDetailsService;
    }

    @Transactional
    public AuthResponse register(String email, String rawPassword) {
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            throw new EmailAlreadyInUseException(email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.getRoles().add(UserRole.USER);
        user = userRepository.save(user);

        Profile profile = new Profile();
        profile.setUser(user);
        profileRepository.save(profile);

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);
        String refreshToken = refreshTokenService.issue(user.getId());

        return buildAuthResponse(authenticatedUser, refreshToken);
    }

    public AuthResponse login(String email, String rawPassword) {
        var authRequest = new UsernamePasswordAuthenticationToken(email, rawPassword);
        var authResult = this.authenticationManager.authenticate(authRequest);
        AuthenticatedUser user = (AuthenticatedUser) authResult.getPrincipal();
        // False positive
        // Authentication call either returns a fully authenticated user or throws an exception.
        // Never returns null
        @SuppressWarnings("DataFlowIssue")
        String refreshToken = refreshTokenService.issue(user.getId());
        return buildAuthResponse(user, refreshToken);
    }

    public AuthResponse refresh(String refreshToken) {
        RefreshResult rotated = refreshTokenService.rotate(refreshToken);
        AuthenticatedUser user = appUserDetailsService.loadUserById(rotated.userId());

        if (!user.isEnabled()) {
            log.warn("User {} is disabled", user.getId());
            throw new BadCredentialsException("Invalid credentials");
        }

        return buildAuthResponse(user, rotated.refreshToken());
    }

    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    private AuthResponse buildAuthResponse(AuthenticatedUser user, String refreshToken) {
        String accessToken = this.jwtService.generateToken(user);
        long expiresInSeconds = jwtProperties.expirationMinutes() * 60;
        return new AuthResponse(accessToken, refreshToken, "Bearer", expiresInSeconds, user.getId(), user.getUsername());
    }
}
