package cl.sonidovivo.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin")
public record AdminProperties(String email, String password, String nombre) {

    public boolean tieneCredenciales() {
        return email != null && !email.isBlank() && password != null && !password.isBlank();
    }

    public String nombreEfectivo() {
        return (nombre == null || nombre.isBlank()) ? "Administrador" : nombre.trim();
    }
}