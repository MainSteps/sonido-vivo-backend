package cl.sonidovivo.api.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final SecurityErrorWriter securityErrorWriter;

    public RestAuthenticationEntryPoint(SecurityErrorWriter securityErrorWriter) {
        this.securityErrorWriter = securityErrorWriter;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        securityErrorWriter.escribir(request, response, HttpStatus.UNAUTHORIZED, "No autenticado");
    }
}