package cl.sonidovivo.api.dto;

import java.time.Instant;

public record LoginResponse(
        String token,
        String tipo,
        Instant expiraEn,
        UsuarioAuthResponse usuario
) {
}