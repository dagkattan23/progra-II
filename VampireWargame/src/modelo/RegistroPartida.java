package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// una fila del historial de partidas terminadas
public class RegistroPartida {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final String jugador1;
    private final String jugador2;
    private final String mensaje;
    private final LocalDateTime fecha;

    public RegistroPartida(String jugador1, String jugador2, String mensaje) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.mensaje = mensaje;
        this.fecha = LocalDateTime.now();
    }

    public String getJugador1() {
        return jugador1;
    }

    public String getJugador2() {
        return jugador2;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getFechaFormateada() {
        return fecha.format(FORMATO_FECHA);
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public boolean participa(String usuario) {
        return jugador1.equalsIgnoreCase(usuario) || jugador2.equalsIgnoreCase(usuario);
    }
}
