package cl.sonidovivo.api.controller;

import cl.sonidovivo.api.dto.ContactoRequest;
import cl.sonidovivo.api.dto.ContactoResponse;
import cl.sonidovivo.api.service.ContactoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contacto")
@Tag(name = "Contacto", description = "Mensajes del formulario de contacto")
public class ContactoController {

    private final ContactoService contactoService;

    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    @PostMapping
    @Operation(summary = "Enviar mensaje de contacto", description = "Público; no requiere JWT.")
    public ResponseEntity<ContactoResponse> crear(@Valid @RequestBody ContactoRequest request) {
        ContactoResponse creado = contactoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    @Operation(summary = "Listar mensajes de contacto", description = "Solo ADMIN (se aplicará en el issue #5).")
    public Page<ContactoResponse> listar(
            @PageableDefault(size = 20, sort = "creadoEn", direction = Sort.Direction.DESC) Pageable pageable) {
        return contactoService.listar(pageable);
    }
}