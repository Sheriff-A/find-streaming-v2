package org.sheriffa.backend.user;

import jakarta.validation.Valid;
import org.sheriffa.backend.security.AuthenticatedUser;
import org.sheriffa.backend.user.dto.MyProfileResponse;
import org.sheriffa.backend.user.dto.PatchProfileRequest;
import org.sheriffa.backend.user.dto.PublicProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<MyProfileResponse> me(@AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(userService.getMyProfile(principal.getId()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<PublicProfileResponse> getPublicUserByEmail(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getPublicProfile(userId));
    }

    @PatchMapping("/me/profile")
    public ResponseEntity<MyProfileResponse> patchMyProfile(@Valid @RequestBody PatchProfileRequest request, @AuthenticationPrincipal AuthenticatedUser principal) {
        return ResponseEntity.ok(userService.patchMyProfile(principal.getId(), request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deactivateMyAccount(@AuthenticationPrincipal AuthenticatedUser principal) {
        userService.deactivate(principal.getId());
        return ResponseEntity.noContent().build();
    }
}
