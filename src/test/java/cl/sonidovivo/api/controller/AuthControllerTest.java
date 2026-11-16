package cl.sonidovivo.api.controller;

import cl.sonidovivo.api.dto.LoginResponse;
import cl.sonidovivo.api.dto.UsuarioAuthResponse;
import cl.sonidovivo.api.entity.Rol;
import cl.sonidovivo.api.exception.CredencialesInvalidasException;
import cl.sonidovivo.api.exception.EmailDuplicadoException;
import cl.sonidovivo.api.security.SecurityConfig;
import cl.sonidovivo.api.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, cl.sonidovivo.api.exception.GlobalExceptionHandler.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    void loginOkDevuelveToken() throws Exception {
        when(authService.login(any())).thenReturn(new LoginResponse(
                "jwt-token",
                "Bearer",
                Instant.parse("2030-01-01T00:00:00Z"),
                new UsuarioAuthResponse(1L, "Admin", "admin@sonidovivo.cl", Rol.ADMIN)));

        String body = """
                {"email":"admin@sonidovivo.cl","password":"Admin123!"}
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.tipo").value("Bearer"));
    }

    @Test
    void registroDuplicadoDevuelve409() throws Exception {
        when(authService.registrar(any())).thenThrow(new EmailDuplicadoException());

        String body = """
                {"nombre":"Ana","email":"ana@test.com","password":"password1"}
                """;

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void loginInvalidoDevuelve401() throws Exception {
        when(authService.login(any())).thenThrow(new CredencialesInvalidasException());

        String body = """
                {"email":"x@y.com","password":"no"}
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}