package datos;

import excepciones.UsuarioDuplicadoException;
import modelo.Jugador;

public class RepositorioJugadoresArreglo implements RepositorioJugadores {

    private static final int CAPACIDAD_MAXIMA = 100;

    private final Jugador[] jugadores;
    private int cantidad;

    public RepositorioJugadoresArreglo() {
        this.jugadores = new Jugador[CAPACIDAD_MAXIMA];
        this.cantidad = 0;
    }

    @Override
    public void agregar(Jugador jugador) throws UsuarioDuplicadoException {
        if (buscarPorUsuario(jugador.getUsuario()) != null) {
            throw new UsuarioDuplicadoException(jugador.getUsuario());
        }
        if (cantidad >= jugadores.length) {
            throw new IllegalStateException("Se alcanzo el numero maximo de jugadores registrados.");
        }
        jugadores[cantidad] = jugador;
        cantidad++;
    }

    @Override
    public Jugador buscarPorUsuario(String usuario) {
        return buscarPorUsuarioRecursivo(usuario, 0);
    }

    // busqueda recursiva en el arreglo en vez de un for
    private Jugador buscarPorUsuarioRecursivo(String usuario, int indice) {
        if (indice >= cantidad) {
            return null;
        }
        if (jugadores[indice].getUsuario().equalsIgnoreCase(usuario)) {
            return jugadores[indice];
        }
        return buscarPorUsuarioRecursivo(usuario, indice + 1);
    }

    @Override
    public Jugador[] listarActivos() {
        Jugador[] temporal = new Jugador[cantidad];
        int total = 0;
        for (int i = 0; i < cantidad; i++) {
            if (jugadores[i].isActivo()) {
                temporal[total] = jugadores[i];
                total++;
            }
        }
        Jugador[] resultado = new Jugador[total];
        System.arraycopy(temporal, 0, resultado, 0, total);
        return resultado;
    }

    @Override
    public int contarJugadores() {
        return cantidad;
    }
}
