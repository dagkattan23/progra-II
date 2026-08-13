package estadopedido;

public enum EstadoPedido {

    REGISTRADO("El pedido ha sido registrado"),
    EN_PREPARACION("El pedido se encuentra en preparacion"),
    ENVIADO("El pedido ha sido enviado"),
    ENTREGADO("El pedido ha sido entregado al cliente"),
    CANCELADO("Pedido cancelado");

    private final String descripcion;

    EstadoPedido(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    
    public boolean puedeTransicionarA(EstadoPedido nuevoEstado) {
        if (this == ENTREGADO || this == CANCELADO) {
           
            return false;
        }

        if (this == REGISTRADO) {
            return nuevoEstado == EN_PREPARACION || nuevoEstado == CANCELADO;
        }

        if (this == EN_PREPARACION) {
            return nuevoEstado == ENVIADO || nuevoEstado == CANCELADO;
        }

        if (this == ENVIADO) {
            return nuevoEstado == ENTREGADO;
        }

        return false;
    }
}