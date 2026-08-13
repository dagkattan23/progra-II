package estadopedido;

public class PedidoInternacional extends Pedido {

    private static final double TARIFA_BASE = 150.0;

    private double costoAduana;
    private String paisDestino;

    public PedidoInternacional(int id, String cliente, double monto, double costoAduana, String paisDestino) {
        super(id, cliente, monto);
        this.costoAduana = costoAduana;
        this.paisDestino = paisDestino;
    }

    @Override
    public double calcularCostoEnvio() {
        return TARIFA_BASE + costoAduana;
    }

    @Override
    public String getTipo() {
        return "Internacional - " + paisDestino;
    }

    @Override
    public String toString() {
        return super.toString() + " | Pais hacia donde se destina: " + paisDestino;
    }
}