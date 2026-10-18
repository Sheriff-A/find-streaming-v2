package org.sheriffa.backend.user;

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
    public void me() {
    }

    @GetMapping("/{email}")
    public void getPublicUserByEmail(@PathVariable String email) {
    }

    @PatchMapping("/me/profile")
    public void patchMyProfile() {
    }

    @DeleteMapping("/me")
    public void deactivateMyAccount() {
    }
}
