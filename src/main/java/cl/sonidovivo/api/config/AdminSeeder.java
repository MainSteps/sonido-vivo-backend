package cl.sonidovivo.api.config;

import cl.sonidovivo.api.entity.Rol;
import cl.sonidovivo.api.entity.Usuario;
import cl.sonidovivo.api.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final AdminProperties adminProperties;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(
            AdminProperties adminProperties,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.adminProperties = adminProperties;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        sembrarAdminSiCorresponde();
    }

    void sembrarAdminSiCorresponde() {
        if (!adminProperties.tieneCredenciales()) {
            log.warn(
                    "No se creó usuario ADMIN: faltan app.admin.email o app.admin.password "
                            + "(variables ADMIN_EMAIL y ADMIN_PASSWORD en prod).");
            return;
        }
        String email = normalizarEmail(adminProperties.email());
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            return;
        }
        Usuario admin = new Usuario();
        admin.setNombre(adminProperties.nombreEfectivo());
        admin.setEmail(email);
        admin.setPasswordHash(passwordEncoder.encode(adminProperties.password()));
        admin.setRol(Rol.ADMIN);
        usuarioRepository.save(admin);
        log.info("Usuario ADMIN inicial creado para el email configurado ({}).", email);
    }

    static String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }
}