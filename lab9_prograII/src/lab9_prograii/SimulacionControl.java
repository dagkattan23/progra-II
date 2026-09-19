package lab9_prograii;


public class SimulacionControl {

    private volatile boolean activo = false;
    private volatile boolean pausado = false;
    private final Object cerrojoPausa = new Object();

    public void iniciar() {
        activo = true;
        pausado = false;
    }

    public void detener() {
        activo = false;
        
        reanudar();
    }

    public void pausar() {
        pausado = true;
    }

    public void reanudar() {
        synchronized (cerrojoPausa) {
            pausado = false;
            cerrojoPausa.notifyAll();
        }
    }

    public boolean isActivo() {
        return activo;
    }

    public boolean isPausado() {
        return pausado;
    }

    
    public void esperarSiPausado() throws InterruptedException {
        synchronized (cerrojoPausa) {
            while (pausado && activo) {
                cerrojoPausa.wait();
            }
        }
    }
}