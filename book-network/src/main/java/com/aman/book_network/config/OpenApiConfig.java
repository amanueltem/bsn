package com.aman.book_network.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        contact = @Contact(
            name = "Amanuel",
            email = "amanueltemesgen62@gmail.com",
            url="https://aman"
        ),
        description = " open api documentation for spring security",
        title = "open api specification- Aman",
        version = "1.0",
        license = @License(
            name = "Licence name",
            url = "https://nothing-todo.com"
        ) ,
        termsOfService = " Terms of service" 
    ),
    servers = {
        @Server(
            description = "Local ENV",
            url = "http://localhost:8088/api/v1"
        ),
        @Server(
            description = "PROD ENV",
            url = "https://aman"
        )
    },
    security = {
        @SecurityRequirement(
            name = "bearerAuth"
        )
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT auth description",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    
}
