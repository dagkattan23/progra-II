package datos;

import modelo.RegistroPartida;

public class RepositorioHistorialArreglo implements RepositorioHistorial {

    private static final int CAPACIDAD_MAXIMA = 200;

    private final RegistroPartida[] registros;
    private int cantidad;

    public RepositorioHistorialArreglo() {
        this.registros = new RegistroPartida[CAPACIDAD_MAXIMA];
        this.cantidad = 0;
    }

    @Override
    public void agregar(RegistroPartida registro) {
        if (cantidad >= registros.length) {
            return;
        }
        registros[cantidad] = registro;
        cantidad++;
    }

    @Override
    public RegistroPartida[] listarPorJugador(String usuario) {
        RegistroPartida[] temporal = new RegistroPartida[cantidad];
        int total = 0;
        // de mas reciente a mas antiguo
        for (int i = cantidad - 1; i >= 0; i--) {
            if (registros[i].participa(usuario)) {
                temporal[total] = registros[i];
                total++;
            }
        }
        RegistroPartida[] resultado = new RegistroPartida[total];
        System.arraycopy(temporal, 0, resultado, 0, total);
        return resultado;
    }
}
