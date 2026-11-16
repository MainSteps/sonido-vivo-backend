package cl.sonidovivo.api.dto;

import cl.sonidovivo.api.entity.Rol;

public record UsuarioAuthResponse(Long id, String nombre, String email, Rol rol) {
}