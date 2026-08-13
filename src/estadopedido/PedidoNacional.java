package estadopedido;

public class PedidoNacional extends Pedido {

    private static final double TARIFA_BASE = 50.0;
    private static final double COSTO_POR_KM = 0.75;

    private double distanciaKm;

    public PedidoNacional(int id, String cliente, double monto, double distanciaKm) {
        super(id, cliente, monto);
        this.distanciaKm = distanciaKm;
    }

    @Override
    public double calcularCostoEnvio() {
        return TARIFA_BASE + (distanciaKm * COSTO_POR_KM);
    }

    @Override
    public String getTipo() {
        return "Nacional";
    }
}