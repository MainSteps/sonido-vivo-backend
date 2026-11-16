package cl.sonidovivo.api.config;

import cl.sonidovivo.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class AdminSeederTest {

    @Autowired
    private AdminSeeder adminSeeder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void noDuplicaAdminSiYaExiste() {
        assertThat(usuarioRepository.existsByEmailIgnoreCase("admin@sonidovivo.cl")).isTrue();
        long cantidadAntes = usuarioRepository.count();

        adminSeeder.sembrarAdminSiCorresponde();

        assertThat(usuarioRepository.count()).isEqualTo(cantidadAntes);
        assertThat(usuarioRepository.findAll().stream()
                .filter(u -> "admin@sonidovivo.cl".equalsIgnoreCase(u.getEmail()))
                .count()).isOne();
    }
}