package empleadofile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Scanner;

public class EmpleadoFile {
    private RandomAccessFile rEmp;

    public EmpleadoFile() throws IOException {
        File root = new File("archivos_empresa");
        if (!root.exists()) {
            root.mkdirs();
        }
        rEmp = new RandomAccessFile("archivos_empresa/empleados.emp", "rw");
    }

    // Método extra para agregar empleados de prueba
    public void addEmployee(int code, String name, double salary) throws IOException {
        rEmp.seek(rEmp.length());
        rEmp.writeInt(code);
        rEmp.writeUTF(name);
        rEmp.writeDouble(salary);
        rEmp.writeLong(Calendar.getInstance().getTimeInMillis());
        rEmp.writeBoolean(true);
        System.out.println("el empleado ha sido agregado para pruebas.");
    }

    // 1. isEmployeeActive
    public boolean isEmployeeActive(int code) throws IOException {
        rEmp.seek(0);
        while (rEmp.getFilePointer() < rEmp.length()) {
            long pos = rEmp.getFilePointer();
            int c = rEmp.readInt();
            String name = rEmp.readUTF();
            double salary = rEmp.readDouble();
            long date = rEmp.readLong();
            boolean active = rEmp.readBoolean();

            if (c == code) {
                if (active) {
                    rEmp.seek(pos + 4);
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public boolean fireEmployee(int code) throws IOException {
        rEmp.seek(0);
        while (rEmp.getFilePointer() < rEmp.length()) {
            int c = rEmp.readInt();
            rEmp.readUTF();
            rEmp.readDouble();
            rEmp.readLong();
            long activePos = rEmp.getFilePointer(); 
            boolean active = rEmp.readBoolean();

            if (c == code) {
                if (active) {
                    rEmp.seek(activePos);
                    rEmp.writeBoolean(false); 
                    return true;
                } else {
                    return false; 
                }
            }
        }
        return false;
    }

    private RandomAccessFile salesFileFor(int code) throws IOException {
        File folder = new File("archivos_empresa/empleado_" + code);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        int year = Calendar.getInstance().get(Calendar.YEAR);
        File file = new File(folder, "ventas" + year + ".emp");
        RandomAccessFile sales = new RandomAccessFile(file, "rw");

        if (sales.length() == 0) {
            for (int i = 0; i < 12; i++) {
                sales.writeDouble(0.0);
                sales.writeBoolean(false);
            }
        }
        return sales;
    }

    public void addSaleToEmployee(int code, double monto) throws IOException {
        if (isEmployeeActive(code)) {
            RandomAccessFile sales = salesFileFor(code);
            int month = Calendar.getInstance().get(Calendar.MONTH);
            
            sales.seek(month * 9);
            double currentSales = sales.readDouble();
            
            sales.seek(month * 9);
            sales.writeDouble(currentSales + monto);
            
            sales.close();
            System.out.println("La venta fue agregada con exito.");
        } else {
            System.out.println("El empleado no existe o no esta activo.");
        }
    }

    public RandomAccessFile billsFilefor(int code) throws IOException {
        File folder = new File("archivos_empresa/empleado_" + code);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, "recibos.emp");
        return new RandomAccessFile(file, "rw");
    }

    public boolean isEmployeePayed(int code) throws IOException {
        RandomAccessFile sales = salesFileFor(code);
        int month = Calendar.getInstance().get(Calendar.MONTH);
        
        sales.seek((month * 9) + 8);
        boolean pagado = sales.readBoolean();
        sales.close();
        
        return pagado;
    }

    public void payEmployee(int code) throws IOException {
        if (!isEmployeeActive(code) || isEmployeePayed(code)) {
            System.out.println("No se pudo pagar");
            return;
        }

        String nombre = rEmp.readUTF();
        double salarioBase = rEmp.readDouble();

        Calendar cal = Calendar.getInstance();
        int mes = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        RandomAccessFile sales = salesFileFor(code);
        sales.seek(mes * 9);
        double ventasMes = sales.readDouble();

        double sueldo = salarioBase + (ventasMes * 0.10);
        double deduccion = sueldo * 0.035;
        double total = sueldo - deduccion;

        RandomAccessFile bills = billsFilefor(code);
        bills.seek(bills.length());
        bills.writeLong(cal.getTimeInMillis());
        bills.writeDouble(sueldo);
        bills.writeDouble(deduccion);
        bills.writeInt(year);
        bills.writeInt(mes);
        bills.close();
        sales.seek((mes * 9) + 8);
        sales.writeBoolean(true);
        sales.close();

        System.out.println("Empleado " + nombre + " se le pago Lps. " + total);
    }

    public void printEmployee(int code) throws IOException {
        rEmp.seek(0);
        boolean existe = false;
        
        while (rEmp.getFilePointer() < rEmp.length()) {
            int c = rEmp.readInt();
            String nombre = rEmp.readUTF();
            double salario = rEmp.readDouble();
            long fecha = rEmp.readLong();
            boolean activo = rEmp.readBoolean();

            if (c == code) {
                existe = true;
                System.out.println("\nCodigo: " + c);
                System.out.println("Nombre: " + nombre);
                System.out.println("Salario: " + salario);
                
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(fecha);
                System.out.println("Fecha de contratacion: " + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR));
                break;
            }
        }

        if (!existe) {
            System.out.println("El empleado no existe.");
            return;
        }
        RandomAccessFile sales = salesFileFor(code);
        double totalAnual = 0;
        System.out.println("\n--- Ventas del año ---");
        
        sales.seek(0);
        for (int i = 0; i < 12; i++) {
            double ventaMes = sales.readDouble();
            sales.readBoolean();
            System.out.println("Mes " + (i + 1) + " : " + ventaMes);
            totalAnual += ventaMes;
        }
        System.out.println("Total de ventas del empleado: " + totalAnual);
        sales.close();
        RandomAccessFile bills = billsFilefor(code);
        int totalRecibos = (int) (bills.length() / 32);
        System.out.println("\nTotal de pagos realizados: " + totalRecibos);
        bills.close();
    }

    public static void main(String[] args) {
        Scanner lea = new Scanner(System.in);
        EmpleadoFile sistema = null;
        
        try {
            sistema = new EmpleadoFile();
        } catch (IOException e) {
            System.out.println("Error al cargar archivos del sistema.");
            return;
        }

        int opcion = 0;
        int codigo;

        do {
            System.out.println("Menu de empleados");
            System.out.println("1. Agregar Empleado (Para pruebas)");
            System.out.println("2. Agregar Venta a Empleado");
            System.out.println("3. Pagar a Empleado");
            System.out.println("4. Imprimir Reporte de Empleado");
            System.out.println("5. Despedir a Empleado");
            System.out.println("6. Salir");
            System.out.print("Ingrese opcion: ");
            
            try {
                opcion = lea.nextInt();
                
                switch (opcion) {
                    case 1:
                        System.out.print("Codigo: ");
                        int cod = lea.nextInt();
                        System.out.print("Nombre: ");
                        lea.nextLine();
                        String nom = lea.nextLine();
                        System.out.print("Salario Base: ");
                        double sal = lea.nextDouble();
                        sistema.addEmployee(cod, nom, sal);
                        break;
                    case 2:
                        System.out.print("Codigo del empleado: ");
                        codigo = lea.nextInt();
                        System.out.print("Monto de la venta: ");
                        double monto = lea.nextDouble();
                        sistema.addSaleToEmployee(codigo, monto);
                        break;
                    case 3:
                        System.out.print("Codigo del empleado a pagar: ");
                        codigo = lea.nextInt();
                        sistema.payEmployee(codigo);
                        break;
                    case 4:
                        System.out.print("Codigo del empleado a buscar: ");
                        codigo = lea.nextInt();
                        sistema.printEmployee(codigo);
                        break;
                    case 5:
                        System.out.print("Codigo del empleado a despedir: ");
                        codigo = lea.nextInt();
                        if (sistema.fireEmployee(codigo)) {
                            System.out.println("Empleado despedido correctamente.");
                        } else {
                            System.out.println("No se pudo despedir (no existe o ya estaba despedido).");
                        }
                        break;
                    case 6:
                        System.out.println("Saliendo del sistema");
                        break;
                    default:
                        System.out.println("Opcion no valida.");
                }
            } catch (Exception e) {
                System.out.println("Error en la operacion: " + e.getMessage());
                lea.nextLine();
            }

        } while (opcion != 6);
    }
}