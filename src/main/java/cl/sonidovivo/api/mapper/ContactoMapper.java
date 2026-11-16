package cl.sonidovivo.api.mapper;

import cl.sonidovivo.api.dto.ContactoRequest;
import cl.sonidovivo.api.dto.ContactoResponse;
import cl.sonidovivo.api.entity.MensajeContacto;
import org.springframework.stereotype.Component;

@Component
public class ContactoMapper {

    public MensajeContacto toEntity(ContactoRequest request) {
        MensajeContacto mensaje = new MensajeContacto();
        mensaje.setNombre(request.nombre());
        mensaje.setEmail(request.email());
        mensaje.setAsunto(request.asunto());
        mensaje.setMensaje(request.mensaje());
        return mensaje;
    }

    public ContactoResponse toResponse(MensajeContacto mensaje) {
        return new ContactoResponse(
                mensaje.getId(),
                mensaje.getNombre(),
                mensaje.getEmail(),
                mensaje.getAsunto(),
                mensaje.getMensaje(),
                mensaje.getCreadoEn());
    }
}