package estadopedido;

public class CapacidadExcedidaException extends Exception {

    public CapacidadExcedidaException(int capacidad) {
        super("Se llego a la capacidad maxima de " + capacidad + " pedidos");
    }
}