package cl.sonidovivo.api.security;

import cl.sonidovivo.api.config.JwtProperties;
import cl.sonidovivo.api.entity.Usuario;
import cl.sonidovivo.api.entity.Rol;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public JwtTokenResult generarToken(Usuario usuario) {
        Instant ahora = Instant.now();
        Instant expira = ahora.plusMillis(jwtProperties.expirationMs());
        String token = Jwts.builder()
                .subject(usuario.getEmail())
                .claim("uid", usuario.getId())
                .claim("rol", usuario.getRol().name())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(expira))
                .signWith(signingKey)
                .compact();
        return new JwtTokenResult(token, expira);
    }

    public UsernamePasswordAuthenticationToken autenticar(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        String rol = claims.get("rol", String.class);
        if (rol == null) {
            throw new IllegalArgumentException("Rol JWT inválido");
        }
        Rol.valueOf(rol);
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + rol));
        return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
    }
}