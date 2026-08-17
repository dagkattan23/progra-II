package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Jugador {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private String usuario;
    private String contrasenia;
    private int puntos;
    private final LocalDateTime fechaIngreso;
    private boolean activo;

    public Jugador(String usuario, String contrasenia) {
        this.usuario = usuario;
        this.contrasenia = contrasenia;
        this.puntos = 0;
        this.fechaIngreso = LocalDateTime.now();
        this.activo = true;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public int getPuntos() {
        return puntos;
    }

    public void sumarPuntos(int cantidad) {
        this.puntos += cantidad;
    }

    public String getFechaIngresoFormateada() {
        return fechaIngreso.format(FORMATO_FECHA);
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return usuario;
    }
}
