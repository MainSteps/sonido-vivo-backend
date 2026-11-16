package cl.sonidovivo.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductoResponse(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Integer stock,
        String categoria,
        String imagenUrl,
        boolean activo,
        Instant creadoEn,
        Instant actualizadoEn
) {
}