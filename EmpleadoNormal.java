package empleado;

public class EmpleadoNormal extends Empleado {

    public EmpleadoNormal(String codigo, String nombre, double salario) {
        super(codigo, nombre, salario);
    }

    @Override
    public double pago() {
        return salario;
    }

    @Override
    public double bono() {
        return salario * 0.05;
    }

    @Override
    public String resumenAnual() {
        return "Empleado normal - bono anual: Lps." + bono();
    }

}