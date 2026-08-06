package empleado;

import java.util.Calendar;

public class EmpleadoTemporal extends Empleado {

    private Calendar finContrato;

    public EmpleadoTemporal(String codigo, String nombre, double salario) {
        super(codigo, nombre, salario);
        finContrato = Calendar.getInstance();
    }

    public void setFinContrato(int anio, int mes, int dia) {

        finContrato.set(Calendar.YEAR, anio);
        finContrato.set(Calendar.MONTH, mes - 1);
        finContrato.set(Calendar.DAY_OF_MONTH, dia);

    }

    @Override
    public String toString() {

        return super.toString() + " fin:" +
                finContrato.get(Calendar.DAY_OF_MONTH) + "/" +
                (finContrato.get(Calendar.MONTH) + 1) + "/" +
                finContrato.get(Calendar.YEAR);

    }

    @Override
    public double pago() {

        Calendar hoy = Calendar.getInstance();

        if (finContrato.after(hoy)) {
            return salario;
        }

        return 0;

    }

    @Override
    public double bono() {

        Calendar hoy = Calendar.getInstance();

        if (!finContrato.after(hoy)) {
            return 0;
        }

        int meses = (finContrato.get(Calendar.YEAR) - hoy.get(Calendar.YEAR)) * 12;
        meses += finContrato.get(Calendar.MONTH) - hoy.get(Calendar.MONTH);

        double bono = salario * 0.02 * meses;

        if (bono > salario * 0.10) {
            bono = salario * 0.10;
        }

        return bono;

    }

    @Override
    public String resumenAnual() {

        if (!finContrato.after(Calendar.getInstance())) {
            return "Empleado temporal - contrato finalizado, sin bono";
        }

        return "Empleado temporal - contrato vigente hasta esta fecha " +
                finContrato.get(Calendar.DAY_OF_MONTH) + "/" +
                (finContrato.get(Calendar.MONTH) + 1) + "/" +
                finContrato.get(Calendar.YEAR) +
                ", bono anual: Lps." + bono();

    }

}