package cl.sonidovivo.api.repository;

import cl.sonidovivo.api.entity.MensajeContacto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeContactoRepository extends JpaRepository<MensajeContacto, Long> {
}