package cl.sonidovivo.api.controller;

import cl.sonidovivo.api.config.OpenApiConfig;
import cl.sonidovivo.api.dto.ProductoRequest;
import cl.sonidovivo.api.dto.ProductoResponse;
import cl.sonidovivo.api.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Catálogo de instrumentos y audio")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Listar productos", description = "Paginación opcional y filtro por categoría.")
    public Page<ProductoResponse> listar(
            @RequestParam(required = false) String categoria,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return productoService.listar(categoria, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por id")
    public ProductoResponse obtener(@PathVariable Long id) {
        return productoService.obtenerPorId(id);
    }

    @PostMapping
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @Operation(summary = "Crear producto", description = "Requiere rol ADMIN.")
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse creado = productoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @Operation(summary = "Actualizar producto", description = "Requiere rol ADMIN.")
    public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
        return productoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    @Operation(summary = "Eliminar producto (lógico)", description = "Marca activo=false. Requiere rol ADMIN.")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}