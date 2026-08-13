package estadopedido;

public class PedidoNoEncontradoException extends Exception {

    public PedidoNoEncontradoException(int id) {
        super("No existe un pedido con id " + id);
    }
}