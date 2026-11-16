package cl.sonidovivo.api.dto;

import java.time.Instant;

public record ContactoResponse(
        Long id,
        String nombre,
        String email,
        String asunto,
        String mensaje,
        Instant creadoEn
) {
}