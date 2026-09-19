package lab9_prograii;

import java.util.Comparator;

public class EmpaquetadorThread extends Thread {

    private static final Comparator<Paquete> POR_PRIORIDAD =
            (a, b) -> b.getPrioridad().getNivel() - a.getPrioridad().getNivel();

    private final CentroLogistico centro;
    private final SimulacionControl control;

    public EmpaquetadorThread(int numero, CentroLogistico centro, SimulacionControl control) {
        super("Empaquetador-" + numero);
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

                Paquete paquete = centro.getListaClasificacion().extraerMayorPrioridad(POR_PRIORIDAD);

                paquete.cambiarEstado(EstadoPaquete.EMPAQUETANDO);
                centro.marcarProcesando(getName(), paquete);
                centro.registrarLog(paquete.getCodigo() + " en empaquetado (" + getName() + ")");

                Thread.sleep(calcularTiempoEmpaquetado(paquete.getPeso()));

                paquete.cambiarEstado(EstadoPaquete.EMPAQUETADO);
                centro.registrarLog(paquete.getCodigo() + " empaquetado");
                centro.marcarLibre(getName());

                centro.getListaEmpaquetado().agregar(paquete);

                Thread.sleep(400);

                Paquete listo = centro.getListaEmpaquetado()
                        .extraerPrimeroQueCumpla(p -> p.getCodigo().equals(paquete.getCodigo()));
                if (listo != null) {
                    listo.cambiarEstado(EstadoPaquete.EN_EXPEDICION);
                    centro.getListaExpedicion().agregar(listo);
                    centro.registrarLog(listo.getCodigo() + " en expedición -> " + listo.getRutaAsignada());
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                centro.registrarLog("Error en " + getName() + ": " + e.getMessage());
            }
        }
        centro.marcarLibre(getName());
    }

    private long calcularTiempoEmpaquetado(double peso) {
        if (peso <= 2.0) {
            return 1000;
        } else if (peso <= 5.0) {
            return 2000;
        } else {
            return 3000;
        }
    }
}