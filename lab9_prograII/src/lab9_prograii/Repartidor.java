package lab9_prograii;

import java.util.concurrent.atomic.AtomicInteger;

public class Repartidor {

    private final int id;
    private final String nombre;
    private final int capacidad;
    private final String ruta;
    private final AtomicInteger cargaActual = new AtomicInteger(0);
    private final AtomicInteger paquetesEntregados = new AtomicInteger(0);

    private volatile EstadoRepartidor estado;

    public Repartidor(int id, String nombre, int capacidad, String ruta) {
        this.id = id;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.ruta = ruta;
        this.estado = EstadoRepartidor.DISPONIBLE;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public String getRuta() {
        return ruta;
    }

    public int getCargaActual() {
        return cargaActual.get();
    }

    public void incrementarCarga() {
        cargaActual.incrementAndGet();
    }

    public void decrementarCarga() {
        cargaActual.decrementAndGet();
    }

    public boolean estaLleno() {
        return cargaActual.get() >= capacidad;
    }

    public int getPaquetesEntregados() {
        return paquetesEntregados.get();
    }

    public void incrementarEntregados() {
        paquetesEntregados.incrementAndGet();
    }

    public EstadoRepartidor getEstado() {
        return estado;
    }

    public void setEstado(EstadoRepartidor estado) {
        this.estado = estado;
    }
}