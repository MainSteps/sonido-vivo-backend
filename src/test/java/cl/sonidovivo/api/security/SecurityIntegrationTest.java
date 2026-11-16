package cl.sonidovivo.api.security;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getProductosSinTokenResponde200() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());
    }

    @Test
    void postProductosSinTokenResponde401() throws Exception {
        String body = """
                {"nombre":"Teclado","precio":1000,"stock":1}
                """;
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("No autenticado"));
    }

    @Test
    void postProductosConTokenInvalidoResponde401() throws Exception {
        String body = """
                {"nombre":"Teclado","precio":1000,"stock":1}
                """;
        mockMvc.perform(post("/api/productos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token-invalido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Token inválido o expirado"));
    }

    @Test
    void postProductosConClienteResponde403() throws Exception {
        String token = registrarYLoginCliente();
        String body = """
                {"nombre":"Teclado","precio":1000,"stock":1}
                """;
        mockMvc.perform(post("/api/productos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Acceso denegado"));
    }

    @Test
    void postProductosConAdminResponde201() throws Exception {
        String token = loginAdmin();
        String body = """
                {"nombre":"Teclado","precio":1000,"stock":1}
                """;
        mockMvc.perform(post("/api/productos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void corsPermiteOrigenLocalhost5173() throws Exception {
        mockMvc.perform(options("/api/productos")
                        .header(HttpHeaders.ORIGIN, "http://localhost:5173")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:5173"));
    }

    private String loginAdmin() throws Exception {
        String body = """
                {"email":"admin@sonidovivo.cl","password":"Admin123!"}
                """;
        String json = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return JsonPath.read(json, "$.token");
    }

    private String registrarYLoginCliente() throws Exception {
        String email = "cliente-" + UUID.randomUUID() + "@test.com";
        String registro = """
                {"nombre":"Cliente","email":"%s","password":"password1"}
                """.formatted(email);
        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registro))
                .andExpect(status().isCreated());
        String login = """
                {"email":"%s","password":"password1"}
                """.formatted(email);
        String json = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(login))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return JsonPath.read(json, "$.token");
    }
}