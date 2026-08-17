package datos;

import excepciones.UsuarioDuplicadoException;
import modelo.Jugador;

public interface RepositorioJugadores {

    void agregar(Jugador jugador) throws UsuarioDuplicadoException;

    Jugador buscarPorUsuario(String usuario);

    Jugador[] listarActivos();

    int contarJugadores();
}
