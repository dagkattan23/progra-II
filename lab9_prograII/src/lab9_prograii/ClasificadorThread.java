package lab9_prograii;

import java.util.Comparator;
import java.util.Random;

public class ClasificadorThread extends Thread {

    private static final Comparator<Paquete> POR_PRIORIDAD =
            (a, b) -> b.getPrioridad().getNivel() - a.getPrioridad().getNivel();

    private final CentroLogistico centro;
    private final SimulacionControl control;
    private final Random random = new Random();

    public ClasificadorThread(int numero, CentroLogistico centro, SimulacionControl control) {
        super("Clasificador-" + numero);
        this.centro = centro;
        this.control = control;
    }

    @Override
    public void run() {
        while (control.isActivo()) {
            try {
                control.esperarSiPausado();
                if (!control.isActivo()) {
                    break;
                }

                Paquete paquete = centro.getListaAlmacen().extraerMayorPrioridad(POR_PRIORIDAD);

                paquete.cambiarEstado(EstadoPaquete.CLASIFICANDO);
                centro.marcarProcesando(getName(), paquete);
                centro.registrarLog(paquete.getCodigo() + " tomado por " + getName());

                Thread.sleep(700 + random.nextInt(600));

                String ruta = centro.determinarRuta(paquete.getCiudad());
                paquete.setRutaAsignada(ruta);
                paquete.cambiarEstado(EstadoPaquete.CLASIFICADO);
                centro.registrarLog(paquete.getCodigo() + " clasificado -> " + ruta);
                centro.marcarLibre(getName());

                centro.getListaClasificacion().agregar(paquete);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                centro.registrarLog("Error en " + getName() + ": " + e.getMessage());
            }
        }
        centro.marcarLibre(getName());
    }
}