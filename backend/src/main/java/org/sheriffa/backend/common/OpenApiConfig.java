package org.sheriffa.backend.common;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@OpenAPIDefinition(
        info = @Info(
                title = "Find Streaming V2 API",
                version = "1.0.0",
                description = "Backend for Find Streaming: auth, users/profiles, and watchlist + items"
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
public class OpenApiConfig {
}
