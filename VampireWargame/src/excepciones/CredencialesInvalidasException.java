package excepciones;

public class CredencialesInvalidasException extends Exception {
    public CredencialesInvalidasException() {
        super("Usuario o contrasena incorrectos.");
    }
}
