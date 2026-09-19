package lab9_prograii;

import java.util.Comparator;
import java.util.Random;


public class RepartidorThread extends Thread {

    private static final Comparator<Paquete> POR_PRIORIDAD =
            (a, b) -> b.getPrioridad().getNivel() - a.getPrioridad().getNivel();

    private static final double PROBABILIDAD_EXITO = 0.8;
    private static final int MAX_INTENTOS = 3;

    private final Repartidor repartidor;
    private final CentroLogistico centro;
    private final SimulacionControl control;
    private final Random random = new Random();

    public RepartidorThread(Repartidor repartidor, CentroLogistico centro, SimulacionControl control) {
        super("Repartidor-" + repartidor.getId());
        this.repartidor = repartidor;
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

                repartidor.setEstado(EstadoRepartidor.DISPONIBLE);
                cargarVehiculo();

                if (repartidor.getCargaActual() == 0) {
                    Thread.sleep(500);
                    continue;
                }

                repartidor.setEstado(EstadoRepartidor.EN_RUTA);
                centro.registrarLog(getName() + " inicia " + repartidor.getRuta()
                        + " con " + repartidor.getCargaActual() + " paquetes");
                Thread.sleep(500 + random.nextInt(500));

                entregarPaquetes();

                repartidor.setEstado(EstadoRepartidor.REGRESANDO);
                centro.registrarLog(getName() + " regresando al centro");
                Thread.sleep(500 + random.nextInt(500));

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                centro.registrarLog("Error en " + getName() + ": " + e.getMessage());
            }
        }
        repartidor.setEstado(EstadoRepartidor.FUERA_DE_SERVICIO);
    }

    private void cargarVehiculo() throws InterruptedException {
        repartidor.setEstado(EstadoRepartidor.CARGANDO);
        int intentosSinPaquetes = 0;

        while (repartidor.getCargaActual() < repartidor.getCapacidad() && intentosSinPaquetes < 6) {
            Paquete paquete = centro.getListaExpedicion().extraerMayorPrioridadQueCumpla(
                    p -> repartidor.getRuta().equals(p.getRutaAsignada()), POR_PRIORIDAD);

            if (paquete == null) {
                intentosSinPaquetes++;
                Thread.sleep(400);
                continue;
            }

            intentosSinPaquetes = 0;
            paquete.cambiarEstado(EstadoPaquete.EN_REPARTO);
            paquete.setRepartidorAsignado(getName());
            repartidor.incrementarCarga();
            centro.getListaReparto().agregar(paquete);
            centro.registrarLog(paquete.getCodigo() + " asignado a " + getName());
        }

        if (repartidor.estaLleno()) {
            centro.registrarLog(getName() + " capacidad completa ("
                    + repartidor.getCapacidad() + "/" + repartidor.getCapacidad() + ") - LLENO");
        }
    }

    private void entregarPaquetes() throws InterruptedException {
        repartidor.setEstado(EstadoRepartidor.ENTREGANDO);
        Paquete paquete;

        while ((paquete = centro.getListaReparto()
                .extraerPrimeroQueCumpla(p -> getName().equals(p.getRepartidorAsignado()))) != null) {

            Thread.sleep(600 + random.nextInt(900));

            boolean entregaExitosa = random.nextDouble() < PROBABILIDAD_EXITO;

            if (entregaExitosa) {
                paquete.cambiarEstado(EstadoPaquete.ENTREGADO);
                repartidor.decrementarCarga();
                repartidor.incrementarEntregados();
                centro.registrarEntrega(paquete);
                centro.getListaEntregados().agregarForzado(paquete);
                centro.registrarLog(paquete.getCodigo() + " entregado");
            } else {
                paquete.incrementarIntentos();
                centro.registrarLog(paquete.getCodigo() + " intento " + paquete.getNumeroIntentos()
                        + " -> cliente ausente");

                if (paquete.getNumeroIntentos() >= MAX_INTENTOS) {
                    paquete.cambiarEstado(EstadoPaquete.DEVUELTO);
                    repartidor.decrementarCarga();
                    centro.registrarDevolucion(paquete);
                    centro.getListaEntregados().agregarForzado(paquete);
                    centro.registrarLog(paquete.getCodigo() + " devuelto");
                } else {
                    paquete.cambiarEstado(EstadoPaquete.NUEVO_INTENTO);
                    paquete.cambiarEstado(EstadoPaquete.EN_REPARTO);
  
                    centro.getListaReparto().agregarForzado(paquete);
                }
            }
        }
    }
}