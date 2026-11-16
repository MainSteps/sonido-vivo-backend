package cl.sonidovivo.api.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SecurityErrorWriter securityErrorWriter;

    public JwtAuthenticationFilter(JwtService jwtService, SecurityErrorWriter securityErrorWriter) {
        this.jwtService = jwtService;
        this.securityErrorWriter = securityErrorWriter;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7).trim();
            if (token.isEmpty()) {
                securityErrorWriter.escribir(
                        request, response, HttpStatus.UNAUTHORIZED, "Token inválido o expirado");
                return;
            }
            try {
                UsernamePasswordAuthenticationToken authentication = jwtService.autenticar(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException | IllegalArgumentException ex) {
                SecurityContextHolder.clearContext();
                securityErrorWriter.escribir(
                        request, response, HttpStatus.UNAUTHORIZED, "Token inválido o expirado");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}