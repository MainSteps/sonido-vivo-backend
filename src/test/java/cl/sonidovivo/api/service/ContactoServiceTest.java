package cl.sonidovivo.api.service;

import cl.sonidovivo.api.dto.ContactoRequest;
import cl.sonidovivo.api.dto.ContactoResponse;
import cl.sonidovivo.api.entity.MensajeContacto;
import cl.sonidovivo.api.mapper.ContactoMapper;
import cl.sonidovivo.api.repository.MensajeContactoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactoServiceTest {

    @Mock
    private MensajeContactoRepository repository;

    @Mock
    private ContactoMapper mapper;

    @InjectMocks
    private ContactoService contactoService;

    @Test
    void crearPersisteYDevuelveDto() {
        ContactoRequest request = new ContactoRequest("Diego", "a@b.com", "Consulta", "Hola");
        MensajeContacto entidad = new MensajeContacto();
        entidad.setId(1L);
        when(mapper.toEntity(request)).thenReturn(entidad);
        when(repository.save(entidad)).thenReturn(entidad);
        when(mapper.toResponse(entidad)).thenReturn(
                new ContactoResponse(1L, "Diego", "a@b.com", "Consulta", "Hola", Instant.now()));

        ContactoResponse respuesta = contactoService.crear(request);

        assertThat(respuesta.id()).isEqualTo(1L);
        assertThat(respuesta.nombre()).isEqualTo("Diego");
    }
}