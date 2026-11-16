package cl.sonidovivo.api.controller;

import cl.sonidovivo.api.dto.ProductoResponse;
import cl.sonidovivo.api.exception.RecursoNoEncontradoException;
import cl.sonidovivo.api.security.SecurityConfig;
import cl.sonidovivo.api.service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
@Import({SecurityConfig.class, cl.sonidovivo.api.exception.GlobalExceptionHandler.class})
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductoService productoService;

    @Test
    void listarDevuelve200() throws Exception {
        when(productoService.listar(eq(null), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(respuesta())));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombre").value("Bajo"));
    }

    @Test
    void obtenerDevuelve404() throws Exception {
        when(productoService.obtenerPorId(404L))
                .thenThrow(new RecursoNoEncontradoException("Producto no encontrado"));

        mockMvc.perform(get("/api/productos/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Producto no encontrado"));
    }

    @Test
    void crearValidoDevuelve201() throws Exception {
        when(productoService.crear(any())).thenReturn(respuesta());

        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "nombre", "Bajo",
                "precio", 100000,
                "stock", 3));

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Bajo"));
    }

    @Test
    void crearInvalidoDevuelve400() throws Exception {
        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "precio", -1,
                "stock", 0));

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores").isArray());
    }

    @Test
    void eliminarDevuelve204() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }

    private ProductoResponse respuesta() {
        return new ProductoResponse(
                1L, "Bajo", null, new BigDecimal("100000"), 3, "cuerdas", null, true,
                Instant.now(), Instant.now());
    }
}