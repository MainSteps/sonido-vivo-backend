package cl.sonidovivo.api.controller;

import cl.sonidovivo.api.dto.ContactoResponse;
import cl.sonidovivo.api.service.ContactoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(cl.sonidovivo.api.exception.GlobalExceptionHandler.class)
class ContactoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ContactoService contactoService;

    @Test
    void crearValidoDevuelve201() throws Exception {
        when(contactoService.crear(any())).thenReturn(
                new ContactoResponse(1L, "Ana", "ana@test.com", "Pedido", "Texto", Instant.now()));

        String body = """
                {"nombre":"Ana","email":"ana@test.com","asunto":"Pedido","mensaje":"Texto"}
                """;

        mockMvc.perform(post("/api/contacto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ana@test.com"));
    }

    @Test
    void crearInvalidoDevuelve400() throws Exception {
        String body = """
                {"nombre":"","email":"no-email","asunto":"","mensaje":""}
                """;

        mockMvc.perform(post("/api/contacto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores").isArray());
    }

    @Test
    void listarDevuelve200() throws Exception {
        when(contactoService.listar(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(
                        new ContactoResponse(1L, "Ana", "a@b.com", "A", "M", Instant.now()))));

        mockMvc.perform(get("/api/contacto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombre").value("Ana"));
    }
}