package cl.sonidovivo.api.exception;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<CampoError> errores
) {
    public ErrorResponse(int status, String error, String message, String path, List<CampoError> errores) {
        this(Instant.now(), status, error, message, path, errores);
    }
}