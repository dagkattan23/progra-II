package estadopedido;

public abstract class Pedido {

    protected int id;
    protected String cliente;
    protected double monto;
    protected EstadoPedido estado;

    public Pedido(int id, String cliente, double monto) {
        this.id = id;
        this.cliente = cliente;
        this.monto = monto;
        this.estado = EstadoPedido.REGISTRADO;
    }

    public abstract double calcularCostoEnvio();

    public abstract String getTipo();

    public int getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public double getMonto() {
        return monto;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Pedido #" + id +
                " | Tipo: " + getTipo() +
                " | Cliente: " + cliente +
                " | Monto: " + monto +
                " | Estado: " + estado + " (" + estado.getDescripcion() + ")" +
                " | Costo de envio: " + calcularCostoEnvio();
    }
}