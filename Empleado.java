package empleado;

import java.util.Calendar;

public abstract class Empleado {

    protected String codigo;
    protected String nombre;
    protected double salario;
    protected Calendar fechaContratacion;

    public Empleado(String codigo, String nombre, double salario) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.salario = salario;
        fechaContratacion = Calendar.getInstance();
    }

    public String getCodigo() {
        return codigo;
    }

    public double getSalario() {
        return salario;
    }

    public String toString() {
        return codigo + " " + nombre + " salario:" + salario +
                " contratado:" +
                fechaContratacion.get(Calendar.DAY_OF_MONTH) + "/" +
                (fechaContratacion.get(Calendar.MONTH) + 1) + "/" +
                fechaContratacion.get(Calendar.YEAR);
    }

    public abstract double pago();

    public abstract double bono();

    public abstract String resumenAnual();

}