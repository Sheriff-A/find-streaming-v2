package org.sheriffa.backend.auth;

import org.sheriffa.backend.common.ConflictException;

public class EmailAlreadyInUseException extends ConflictException {
    public EmailAlreadyInUseException(String email) {
        super("Email already in use: " + email);
    }
}
