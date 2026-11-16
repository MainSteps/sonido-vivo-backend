package cl.sonidovivo.api.service;

import cl.sonidovivo.api.dto.LoginRequest;
import cl.sonidovivo.api.dto.RegistroRequest;
import cl.sonidovivo.api.entity.Rol;
import cl.sonidovivo.api.entity.Usuario;
import cl.sonidovivo.api.exception.CredencialesInvalidasException;
import cl.sonidovivo.api.exception.EmailDuplicadoException;
import cl.sonidovivo.api.repository.UsuarioRepository;
import cl.sonidovivo.api.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registroDuplicadoLanza409() {
        when(usuarioRepository.existsByEmailIgnoreCase("a@b.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.registrar(
                new RegistroRequest("Ana", "a@b.com", "password1")))
                .isInstanceOf(EmailDuplicadoException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void loginConClaveIncorrectaLanza401() {
        Usuario usuario = new Usuario();
        usuario.setPasswordHash("hash");
        when(usuarioRepository.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("mala", "hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("a@b.com", "mala")))
                .isInstanceOf(CredencialesInvalidasException.class);
    }

    @Test
    void registroAsignaRolCliente() {
        when(usuarioRepository.existsByEmailIgnoreCase(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(10L);
            return u;
        });

        var respuesta = authService.registrar(new RegistroRequest("Pedro", "Pedro@mail.com", "password1"));

        assertThat(respuesta.rol()).isEqualTo(Rol.CLIENTE);
        assertThat(respuesta.email()).isEqualTo("pedro@mail.com");
    }
}