package org.sheriffa.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    // Derived query.
    // Spring Data parses the method name into a query

    // IgnoreCase matters here because the db's uniqueness is enforced on LOWER(email)
    // Needs to match that, or you could "find no user"
    Optional<User> findByEmailIgnoreCase(String email);
}
