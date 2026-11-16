package cl.sonidovivo.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_AUTH = "bearerAuth";

    @Bean
    OpenAPI sonidoVivoOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sonido Vivo API")
                        .version("0.0.1-SNAPSHOT")
                        .description("API REST de la tienda Sonido Vivo (instrumentos y audio). "
                                + "Los endpoints protegidos requieren un JWT en la cabecera "
                                + "`Authorization: Bearer <token>`.")
                        .contact(new Contact()
                                .name("Diego Alonso Pazos Atarama")
                                .email("dapa.diego23@gmail.com")))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtenido desde POST /api/auth/login")));
    }
}