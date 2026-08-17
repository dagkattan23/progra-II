package excepciones;

// se lanza si la contrasena no tiene 5 caracteres
public class PasswordInvalidaException extends Exception {
    public PasswordInvalidaException() {
        super("La contrasena debe tener exactamente 5 caracteres.");
    }
}
