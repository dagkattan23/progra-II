package empleado;

public class EmpleadoPorVenta extends Empleado {

    private double ventas;

    public EmpleadoPorVenta(String codigo, String nombre, double salario) {
        super(codigo, nombre, salario);
        ventas = 0;
    }

    public void registrarVenta(double venta) {
        ventas += venta;
    }

    @Override
    public double pago() {
        return salario + (ventas * 0.10);
    }

    @Override
    public double bono() {
        return ventas * 0.05;
    }

    @Override
    public String resumenAnual() {
        return "Empleado por venta - bono anual: Lps." + bono();
    }

    @Override
    public String toString() {
        return super.toString() + " ventas:" + ventas;
    }

}