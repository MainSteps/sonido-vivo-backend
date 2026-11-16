package cl.sonidovivo.api.controller;

import cl.sonidovivo.api.dto.LoginRequest;
import cl.sonidovivo.api.dto.LoginResponse;
import cl.sonidovivo.api.dto.RegistroRequest;
import cl.sonidovivo.api.dto.UsuarioAuthResponse;
import cl.sonidovivo.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro e inicio de sesión JWT")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    @Operation(summary = "Registrar cliente", description = "Crea un usuario con rol CLIENTE.")
    public ResponseEntity<UsuarioAuthResponse> registro(@Valid @RequestBody RegistroRequest request) {
        UsuarioAuthResponse usuario = authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Devuelve un JWT Bearer si las credenciales son válidas.")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}