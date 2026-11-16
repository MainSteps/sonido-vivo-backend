package cl.sonidovivo.api.security;

import java.time.Instant;

public record JwtTokenResult(String token, Instant expiraEn) {
}