package cl.sonidovivo.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
        String nombre,

        @Size(max = 2000, message = "La descripción no puede superar 2000 caracteres")
        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El precio debe ser mayor o igual a 0")
        BigDecimal precio,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
        Integer stock,

        @Size(max = 100, message = "La categoría no puede superar 100 caracteres")
        String categoria,

        @Size(max = 500, message = "La URL de imagen no puede superar 500 caracteres")
        String imagenUrl
) {
}