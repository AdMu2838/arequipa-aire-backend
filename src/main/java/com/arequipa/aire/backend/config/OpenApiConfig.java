package com.arequipa.aire.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI/Swagger.
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Sistema de Monitoreo de Calidad del Aire - Arequipa",
        version = "1.0.0",
        description = """
            API REST para el Sistema Inteligente de Monitoreo y Predicción de Calidad del Aire Urbano de Arequipa.
            
            Funcionalidades principales:
            - Consulta de datos de calidad del aire en tiempo real
            - Gestión de alertas personalizadas por usuario
            - Predicciones de calidad del aire usando machine learning
            - Autenticación y autorización con JWT
            - Datos históricos con paginación
            - Mapas interactivos con información de estaciones
            """,
        contact = @Contact(
            name = "Equipo Arequipa Aire",
            email = "soporte@arequipa-aire.com",
            url = "https://arequipa-aire.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080/api", description = "Servidor de desarrollo"),
        @Server(url = "https://api.arequipa-aire.com", description = "Servidor de producción")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Autenticación JWT. Incluir el token en el header Authorization como 'Bearer {token}'"
)
public class OpenApiConfig {
}