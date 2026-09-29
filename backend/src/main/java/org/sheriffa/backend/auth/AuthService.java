package org.sheriffa.backend.auth;

import org.sheriffa.backend.auth.dto.AuthResponse;
import org.sheriffa.backend.security.AuthenticatedUser;
import org.sheriffa.backend.security.JwtProperties;
import org.sheriffa.backend.security.JwtService;
import org.sheriffa.backend.user.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    public AuthService(
            UserRepository userRepository,
            ProfileRepository profileRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            JwtProperties jwtProperties
    ) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
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

        return buildAuthResponse(new AuthenticatedUser(user));
    }

    public AuthResponse login(String email, String rawPassword) {
        var authRequest = new UsernamePasswordAuthenticationToken(email, rawPassword);
        var authResult = this.authenticationManager.authenticate(authRequest);
        return buildAuthResponse((AuthenticatedUser) authResult.getPrincipal());
    }

    private AuthResponse buildAuthResponse(AuthenticatedUser user) {
        String token = this.jwtService.generateToken(user);
        long expiresInSeconds = jwtProperties.expirationMinutes() * 60;
        return new AuthResponse(token, "Bearer", expiresInSeconds, user.getId(), user.getUsername());
    }
}
