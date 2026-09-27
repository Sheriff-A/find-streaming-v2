package org.sheriffa.backend.common;

public record PutResponseState<T>(
        T entity,
        boolean created
) {
}
