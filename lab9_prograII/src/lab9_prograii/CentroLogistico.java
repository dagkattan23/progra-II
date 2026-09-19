package lab9_prograii;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


public class CentroLogistico {

    public static final int CAP_RECEPCION = 10;
    public static final int CAP_ALMACEN = 20;
    public static final int CAP_CLASIFICACION = 10;
    public static final int CAP_EMPAQUETADO = 8;
    public static final int CAP_EXPEDICION = 15;
    
    public static final int CAP_REPARTO = 20;
    public static final int CAP_ENTREGADOS = 1000;
    public static final int CAP_REGISTRO = 250;

    private final ListaEnlazada<Paquete> listaRecepcion = new ListaEnlazada<>(CAP_RECEPCION);
    private final ListaEnlazada<Paquete> listaAlmacen = new ListaEnlazada<>(CAP_ALMACEN);
    private final ListaEnlazada<Paquete> listaClasificacion = new ListaEnlazada<>(CAP_CLASIFICACION);
    private final ListaEnlazada<Paquete> listaEmpaquetado = new ListaEnlazada<>(CAP_EMPAQUETADO);
    private final ListaEnlazada<Paquete> listaExpedicion = new ListaEnlazada<>(CAP_EXPEDICION);
    private final ListaEnlazada<Paquete> listaReparto = new ListaEnlazada<>(CAP_REPARTO);
    private final ListaEnlazada<Paquete> listaEntregados = new ListaEnlazada<>(CAP_ENTREGADOS);
    private final ListaEnlazada<String> registro = new ListaEnlazada<>(CAP_REGISTRO);

    private final ConcurrentHashMap<String, Paquete> procesamientoActual = new ConcurrentHashMap<>();

    private final AtomicInteger totalGenerados = new AtomicInteger(0);
    private final AtomicInteger totalEntregados = new AtomicInteger(0);
    private final AtomicInteger totalDevueltos = new AtomicInteger(0);
    private final AtomicLong sumaTiempos = new AtomicLong(0);
    private final AtomicInteger contadorTiempos = new AtomicInteger(0);

    private final DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm:ss");

    public String determinarRuta(String ciudad) {
        switch (ciudad) {
            case "Barcelona Centro":
                return "Ruta 1";
            case "Eixample":
                return "Ruta 1";
            case "Gràcia":
                return "Ruta 2";
            case "Sant Martí":
                return "Ruta 3";
            case "Badalona":
                return "Ruta 4";
            default:
                return "Ruta 1";
        }
    }

    public void registrarLog(String mensaje) {
        String hora = LocalTime.now().format(formatoHora);
        registro.agregarDescartandoAntiguo(hora + " | " + mensaje);
    }

    public void marcarProcesando(String trabajador, Paquete paquete) {
        procesamientoActual.put(trabajador, paquete);
    }

    public void marcarLibre(String trabajador) {
        procesamientoActual.remove(trabajador);
    }

    public Paquete getProcesando(String trabajador) {
        return procesamientoActual.get(trabajador);
    }

    public void incrementarGenerados() {
        totalGenerados.incrementAndGet();
    }

    public void registrarEntrega(Paquete paquete) {
        totalEntregados.incrementAndGet();
        long segundos = (System.currentTimeMillis() - paquete.getTimestampCreacion()) / 1000;
        sumaTiempos.addAndGet(segundos);
        contadorTiempos.incrementAndGet();
    }

    public void registrarDevolucion(Paquete paquete) {
        totalDevueltos.incrementAndGet();
    }

    public double getTiempoPromedio() {
        int c = contadorTiempos.get();
        return c == 0 ? 0.0 : (double) sumaTiempos.get() / c;
    }

    public int getTotalGenerados() {
        return totalGenerados.get();
    }

    public int getTotalEntregados() {
        return totalEntregados.get();
    }

    public int getTotalDevueltos() {
        return totalDevueltos.get();
    }

    public ListaEnlazada<Paquete> getListaRecepcion() {
        return listaRecepcion;
    }

    public ListaEnlazada<Paquete> getListaAlmacen() {
        return listaAlmacen;
    }

    public ListaEnlazada<Paquete> getListaClasificacion() {
        return listaClasificacion;
    }

    public ListaEnlazada<Paquete> getListaEmpaquetado() {
        return listaEmpaquetado;
    }

    public ListaEnlazada<Paquete> getListaExpedicion() {
        return listaExpedicion;
    }

    public ListaEnlazada<Paquete> getListaReparto() {
        return listaReparto;
    }

    public ListaEnlazada<Paquete> getListaEntregados() {
        return listaEntregados;
    }

    public ListaEnlazada<String> getRegistro() {
        return registro;
    }

    public void reiniciar() {
        listaRecepcion.vaciar();
        listaAlmacen.vaciar();
        listaClasificacion.vaciar();
        listaEmpaquetado.vaciar();
        listaExpedicion.vaciar();
        listaReparto.vaciar();
        listaEntregados.vaciar();
        registro.vaciar();
        procesamientoActual.clear();
        totalGenerados.set(0);
        totalEntregados.set(0);
        totalDevueltos.set(0);
        sumaTiempos.set(0);
        contadorTiempos.set(0);
    }
}