package empleado;

import java.util.ArrayList;
import java.util.Scanner;

public class Empresa {

    private static ArrayList<Empleado> empleados = new ArrayList<>();
    private static Scanner lea = new Scanner(System.in); 

 
    private static Empleado buscar(String codigo) {

        for (Empleado e : empleados) {

            if (e.getCodigo().equals(codigo)) {
                return e;
            }

        }

        return null;
    }


    public static void agregarEmpleado(String codigo, String nombre, String tipo) {

        if (buscar(codigo) == null) {

            System.out.print("Favor ngresar salario: ");
            double salario = lea.nextDouble();

            if (tipo.equalsIgnoreCase("Normal")) {

                empleados.add(new EmpleadoNormal(codigo, nombre, salario));

            } else if (tipo.equalsIgnoreCase("Temporal")) {

                empleados.add(new EmpleadoTemporal(codigo, nombre, salario));

            } else {

                empleados.add(new EmpleadoPorVenta(codigo, nombre, salario));

            }

        }

    }


    public static double pagarEmpleado(String codigo) {

        Empleado e = buscar(codigo);

        if (e != null) {
            return e.pago();
        }

        return 0;

    }


    public static void registrarVenta(String codigo, double venta) {

        Empleado e = buscar(codigo);

        if (e != null && e instanceof EmpleadoPorVenta) {

            EmpleadoPorVenta ev = (EmpleadoPorVenta) e;
            ev.registrarVenta(venta);

        }

    }


    public static void setFechaFin(String codigo, int anio, int mes, int dia) {

        Empleado e = buscar(codigo);

        if (e != null && e instanceof EmpleadoTemporal) {

            EmpleadoTemporal et = (EmpleadoTemporal) e;
            et.setFinContrato(anio, mes, dia);

        }

    }


    public static void imprimir() {

        int e = 0;
        int et = 0;
        int ev = 0;


        for (Empleado emp : empleados) {

            System.out.println(emp);


            if (emp instanceof EmpleadoNormal) {

                e++;

            } else if (emp instanceof EmpleadoTemporal) {

                et++;

            } else if (emp instanceof EmpleadoPorVenta) {

                ev++;

            }

        }


        System.out.println(e + "-" + et + "-" + ev);

    }



    public static void main(String[] args) {


        

        agregarEmpleado("001", "Daniel", "NORMAL");

        agregarEmpleado("002", "Antonio", "TEMPORAL");

        agregarEmpleado("003", "Guevara", "VENTA");

        agregarEmpleado("004", "Kattan", "TEMPORAL");

        setFechaFin("002", 2027, 12, 31);

        setFechaFin("004", 2025, 1, 1);
        
        registrarVenta("003", 20000);

        System.out.println("Pago empleado normal: "
                + pagarEmpleado("001"));

        System.out.println("Pago empleado temporal vigente: "
                + pagarEmpleado("002"));

        System.out.println("Pago empleado por venta: "
                + pagarEmpleado("003"));

        System.out.println("Pago empleado temporal vencido: "
                + pagarEmpleado("004"));



        System.out.println();



        

        for (Empleado emp : empleados) {

            System.out.println(emp.resumenAnual());

            System.out.println("Bono: " + emp.bono());

            System.out.println();

        }



        

        imprimir();


    }

}
