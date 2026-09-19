package lab9_prograii;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;


public class Paquete {

    private static final Map<EstadoPaquete, Set<EstadoPaquete>> TRANSICIONES = new EnumMap<>(EstadoPaquete.class);
    static {
        TRANSICIONES.put(EstadoPaquete.RECIBIDO, EnumSet.of(EstadoPaquete.ALMACENADO));
        TRANSICIONES.put(EstadoPaquete.ALMACENADO, EnumSet.of(EstadoPaquete.CLASIFICANDO));
        TRANSICIONES.put(EstadoPaquete.CLASIFICANDO, EnumSet.of(EstadoPaquete.CLASIFICADO));
        TRANSICIONES.put(EstadoPaquete.CLASIFICADO, EnumSet.of(EstadoPaquete.EMPAQUETANDO));
        TRANSICIONES.put(EstadoPaquete.EMPAQUETANDO, EnumSet.of(EstadoPaquete.EMPAQUETADO));
        TRANSICIONES.put(EstadoPaquete.EMPAQUETADO, EnumSet.of(EstadoPaquete.EN_EXPEDICION));
        TRANSICIONES.put(EstadoPaquete.EN_EXPEDICION, EnumSet.of(EstadoPaquete.EN_REPARTO));
        TRANSICIONES.put(EstadoPaquete.EN_REPARTO,
                EnumSet.of(EstadoPaquete.ENTREGADO, EstadoPaquete.NUEVO_INTENTO, EstadoPaquete.DEVUELTO));
        TRANSICIONES.put(EstadoPaquete.NUEVO_INTENTO, EnumSet.of(EstadoPaquete.EN_REPARTO));
        TRANSICIONES.put(EstadoPaquete.ENTREGADO, EnumSet.noneOf(EstadoPaquete.class));
        TRANSICIONES.put(EstadoPaquete.DEVUELTO, EnumSet.noneOf(EstadoPaquete.class));
    }

    private final String codigo;
    private final String cliente;
    private final String direccion;
    private final String ciudad;
    private final double peso;
    private final Prioridad prioridad;
    private final long timestampCreacion;

    private volatile EstadoPaquete estado;
    private volatile String rutaAsignada;
    private volatile int numeroIntentos;
    private volatile String repartidorAsignado;

    public Paquete(String codigo, String cliente, String direccion, String ciudad,
                   double peso, Prioridad prioridad) {
        this.codigo = codigo;
        this.cliente = cliente;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.peso = peso;
        this.prioridad = prioridad;
        this.estado = EstadoPaquete.RECIBIDO;
        this.rutaAsignada = null;
        this.numeroIntentos = 0;
        this.timestampCreacion = System.currentTimeMillis();
    }

    
    public synchronized void cambiarEstado(EstadoPaquete nuevoEstado) {
        Set<EstadoPaquete> permitidos = TRANSICIONES.get(this.estado);
        if (permitidos == null || !permitidos.contains(nuevoEstado)) {
            throw new IllegalStateException(
                    "Transición inválida para " + codigo + ": " + this.estado + " -> " + nuevoEstado);
        }
        this.estado = nuevoEstado;
    }

    public void incrementarIntentos() {
        numeroIntentos++;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCliente() {
        return cliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public double getPeso() {
        return peso;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public EstadoPaquete getEstado() {
        return estado;
    }

    public String getRutaAsignada() {
        return rutaAsignada;
    }

    public void setRutaAsignada(String rutaAsignada) {
        this.rutaAsignada = rutaAsignada;
    }

    public int getNumeroIntentos() {
        return numeroIntentos;
    }

    public String getRepartidorAsignado() {
        return repartidorAsignado;
    }

    public void setRepartidorAsignado(String repartidorAsignado) {
        this.repartidorAsignado = repartidorAsignado;
    }

    public long getTimestampCreacion() {
        return timestampCreacion;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %.1fkg | %s | %s",
                codigo, cliente, ciudad, peso, prioridad, estado);
    }
}