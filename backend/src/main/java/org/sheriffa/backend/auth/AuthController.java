package org.sheriffa.backend.auth;

import jakarta.validation.Valid;
import org.sheriffa.backend.auth.dto.AuthResponse;
import org.sheriffa.backend.auth.dto.LoginRequest;
import org.sheriffa.backend.auth.dto.MeResponse;
import org.sheriffa.backend.auth.dto.RegisterRequest;
import org.sheriffa.backend.security.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request.email(), request.password());
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request.email(), request.password());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(@AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(MeResponse.from(principal));
    }
}
