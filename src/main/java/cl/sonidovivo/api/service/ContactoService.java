package cl.sonidovivo.api.service;

import cl.sonidovivo.api.dto.ContactoRequest;
import cl.sonidovivo.api.dto.ContactoResponse;
import cl.sonidovivo.api.entity.MensajeContacto;
import cl.sonidovivo.api.mapper.ContactoMapper;
import cl.sonidovivo.api.repository.MensajeContactoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactoService {

    private final MensajeContactoRepository mensajeContactoRepository;
    private final ContactoMapper contactoMapper;

    public ContactoService(
            MensajeContactoRepository mensajeContactoRepository,
            ContactoMapper contactoMapper) {
        this.mensajeContactoRepository = mensajeContactoRepository;
        this.contactoMapper = contactoMapper;
    }

    @Transactional
    public ContactoResponse crear(ContactoRequest request) {
        MensajeContacto guardado = mensajeContactoRepository.save(contactoMapper.toEntity(request));
        return contactoMapper.toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public Page<ContactoResponse> listar(Pageable pageable) {
        return mensajeContactoRepository.findAll(pageable).map(contactoMapper::toResponse);
    }
}