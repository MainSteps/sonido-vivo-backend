package cl.sonidovivo.api.service;

import cl.sonidovivo.api.dto.LoginRequest;
import cl.sonidovivo.api.dto.LoginResponse;
import cl.sonidovivo.api.dto.RegistroRequest;
import cl.sonidovivo.api.dto.UsuarioAuthResponse;
import cl.sonidovivo.api.entity.Rol;
import cl.sonidovivo.api.entity.Usuario;
import cl.sonidovivo.api.exception.CredencialesInvalidasException;
import cl.sonidovivo.api.exception.EmailDuplicadoException;
import cl.sonidovivo.api.repository.UsuarioRepository;
import cl.sonidovivo.api.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public UsuarioAuthResponse registrar(RegistroRequest request) {
        String email = normalizarEmail(request.email());
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailDuplicadoException();
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setEmail(email);
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuario.setRol(Rol.CLIENTE);
        Usuario guardado = usuarioRepository.save(usuario);
        return toUsuarioAuthResponse(guardado);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = normalizarEmail(request.email());
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(CredencialesInvalidasException::new);
        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }
        var jwt = jwtService.generarToken(usuario);
        return new LoginResponse(
                jwt.token(),
                "Bearer",
                jwt.expiraEn(),
                toUsuarioAuthResponse(usuario));
    }

    private static String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private UsuarioAuthResponse toUsuarioAuthResponse(Usuario usuario) {
        return new UsuarioAuthResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol());
    }
}