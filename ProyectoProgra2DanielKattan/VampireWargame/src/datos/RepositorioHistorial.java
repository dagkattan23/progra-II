package datos;

import modelo.RegistroPartida;

public interface RepositorioHistorial {

    void agregar(RegistroPartida registro);

    RegistroPartida[] listarPorJugador(String usuario);
}
