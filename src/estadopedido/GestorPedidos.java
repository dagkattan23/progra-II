package estadopedido;

public class GestorPedidos {

    private Pedido[] pedidos;
    private int cantidad;

    public GestorPedidos(int capacidad) {
        pedidos = new Pedido[capacidad];
        cantidad = 0;
    }

    public void agregarPedido(Pedido p) throws CapacidadExcedidaException {
        if (cantidad == pedidos.length) {
            throw new CapacidadExcedidaException(pedidos.length);
        }
        pedidos[cantidad] = p;
        cantidad++;
    }

    public Pedido buscarPorId(int id) throws PedidoNoEncontradoException {
        for (int i = 0; i < cantidad; i++) {
            if (pedidos[i].getId() == id) {
                return pedidos[i];
            }
        }
        throw new PedidoNoEncontradoException(id);
    }

    public void cambiarEstado(int id, EstadoPedido nuevoEstado)
            throws PedidoNoEncontradoException, TransicionEstadoInvalidaException {

        Pedido p = buscarPorId(id);

        if (!p.getEstado().puedeTransicionarA(nuevoEstado)) {
            throw new TransicionEstadoInvalidaException(p.getEstado(), nuevoEstado);
        }

        p.setEstado(nuevoEstado);
    }

    public Pedido[] listarPorEstado(EstadoPedido estado) {
        int contador = 0;
        for (int i = 0; i < cantidad; i++) {
            if (pedidos[i].getEstado() == estado) {
                contador++;
            }
        }

        Pedido[] resultado = new Pedido[contador];
        int j = 0;
        for (int i = 0; i < cantidad; i++) {
            if (pedidos[i].getEstado() == estado) {
                resultado[j] = pedidos[i];
                j++;
            }
        }

        return resultado;
    }

    // metodo extra para poder listar todo en el menu
    public Pedido[] listarTodos() {
        Pedido[] resultado = new Pedido[cantidad];
        for (int i = 0; i < cantidad; i++) {
            resultado[i] = pedidos[i];
        }
        return resultado;
    }

    public int getCantidad() {
        return cantidad;
    }
}