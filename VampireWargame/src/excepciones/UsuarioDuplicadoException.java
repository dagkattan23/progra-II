package excepciones;

// se lanza si el usuario ya existe
public class UsuarioDuplicadoException extends Exception {
    public UsuarioDuplicadoException(String usuario) {
        super("Ya existe un jugador registrado con el usuario \"" + usuario + "\".");
    }
}
