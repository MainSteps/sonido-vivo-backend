package cl.sonidovivo.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "app.cors")
public record CorsProperties(String origins) {

    public List<String> allowedOrigins() {
        if (origins == null || origins.isBlank()) {
            return List.of();
        }
        return Arrays.stream(origins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}