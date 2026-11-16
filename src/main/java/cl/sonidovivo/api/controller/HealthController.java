package cl.sonidovivo.api.controller;

import cl.sonidovivo.api.dto.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Salud", description = "Comprobación de que la API está operativa")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Estado de la API", description = "Devuelve si el backend está en ejecución.")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(new HealthResponse("UP", "sonido-vivo-backend"));
    }
}