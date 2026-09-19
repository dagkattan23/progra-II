package lab9_prograii;

import java.util.Random;

public class RecepcionThread extends Thread {

    private static final String[] CLIENTES = {
            "Daniel Kattan", "Camila Fajardo", "Lionel Messi", "Michelle Kattan", "Andres Kattan",
            "Antonio Kattan", "Rapphael Dias", "Osmar Rivera", "Miguel Ortega", "Lucía Molina"
    };

    private static final String[] CIUDADES = {
            "Barcelona Centro", "Eixample", "Gràcia", "Sant Martí", "Badalona"
    };

    private static final String[] CALLES = {
            "Calle Mayor", "Avenida Diagonal", "Calle Aragón", "Paseo de Gracia",
            "Calle Balmes", "Avenida Meridiana", "Calle Muntaner"
    };

    private final CentroLogistico centro;
    private final SimulacionControl control;
    private final Random random = new Random();
    private int contador = 0;

    public RecepcionThread(CentroLogistico centro, SimulacionControl control) {
        super("Hilo-Recepcion");
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

                contador++;
                String codigo = String.format("PKG-%03d", contador);
                Paquete paquete = generarPaquete(codigo);

                centro.registrarLog(codigo + " recibido");
                centro.getListaRecepcion().agregar(paquete);
                centro.incrementarGenerados();

                Thread.sleep(400 + random.nextInt(400));

                Paquete extraido = centro.getListaRecepcion().extraerPrimero();
                extraido.cambiarEstado(EstadoPaquete.ALMACENADO);
                centro.getListaAlmacen().agregar(extraido);
                centro.registrarLog(codigo + " almacenado");

                Thread.sleep(1000 + random.nextInt(2000));

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                centro.registrarLog("Error en " + getName() + ": " + e.getMessage());
            }
        }
    }

    private Paquete generarPaquete(String codigo) {
        String cliente = CLIENTES[random.nextInt(CLIENTES.length)];
        String ciudad = CIUDADES[random.nextInt(CIUDADES.length)];
        String direccion = CALLES[random.nextInt(CALLES.length)] + " " + (1 + random.nextInt(200));
        double peso = Math.round((0.5 + random.nextDouble() * 7.5) * 10.0) / 10.0;
        Prioridad prioridad = Prioridad.values()[random.nextInt(Prioridad.values().length)];
        return new Paquete(codigo, cliente, direccion, ciudad, peso, prioridad);
    }
}