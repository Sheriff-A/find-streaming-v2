package org.sheriffa.backend.user;

import org.sheriffa.backend.common.NotFoundException;

import java.util.UUID;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(UUID id) {
        super("User with ID: " + id + " not found");
    }
}
