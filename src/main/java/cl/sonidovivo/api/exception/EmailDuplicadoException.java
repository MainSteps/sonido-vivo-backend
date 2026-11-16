package cl.sonidovivo.api.exception;

public class EmailDuplicadoException extends RuntimeException {

    public EmailDuplicadoException() {
        super("El email ya está registrado");
    }
}