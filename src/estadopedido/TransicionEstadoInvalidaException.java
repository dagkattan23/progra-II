package estadopedido;

public class TransicionEstadoInvalidaException extends Exception {

    public TransicionEstadoInvalidaException(EstadoPedido actual, EstadoPedido nuevo) {
        super("No se puede pasar del estado " + actual + " al estado " + nuevo);
    }
}
