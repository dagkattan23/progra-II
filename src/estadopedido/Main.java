package estadopedido;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        GestorPedidos gestor = new GestorPedidos(10);
        int opcion;

        do {
            System.out.println("Sistema para llevar la gestion de pedidos");
            System.out.println("1. Agregar pedido nacional");
            System.out.println("2. Agregar pedido internacional");
            System.out.println("3. Buscar pedido por numero de id");
            System.out.println("4. Cambiar el estado de un pedido");
            System.out.println("5. Listar los pedidos por estado");
            System.out.println("6. Listar todos los pedidos");
            System.out.println("7. Salir");
            System.out.print("Favor elegir una opcion: ");

            opcion = leerEntero(sc);

            switch (opcion) {
                case 1:
                    agregarNacional(sc, gestor);
                    break;
                case 2:
                    agregarInternacional(sc, gestor);
                    break;
                case 3:
                    buscarPedido(sc, gestor);
                    break;
                case 4:
                    cambiarEstado(sc, gestor);
                    break;
                case 5:
                    listarPorEstado(sc, gestor);
                    break;
                case 6:
                    listarTodos(gestor);
                    break;
                case 7:
                    System.out.println("Adios!");
                    break;
                default:
                    System.out.println("La opcion que eligio no es valida");
            }

        } while (opcion != 0);

        sc.close();
    }

    private static int leerEntero(Scanner sc) {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void agregarNacional(Scanner sc, GestorPedidos gestor) {
        System.out.print("Id: ");
        int id = leerEntero(sc);
        System.out.print("Cliente: ");
        String cliente = sc.nextLine();
        System.out.print("Monto: ");
        double monto = Double.parseDouble(sc.nextLine());
        System.out.print("Distancia en km: ");
        double distancia = Double.parseDouble(sc.nextLine());

        Pedido p = new PedidoNacional(id, cliente, monto, distancia);

        try {
            gestor.agregarPedido(p);
            System.out.println("Pedido nacional agregado.");
        } catch (CapacidadExcedidaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void agregarInternacional(Scanner sc, GestorPedidos gestor) {
        System.out.print("Id: ");
        int id = leerEntero(sc);
        System.out.print("Cliente: ");
        String cliente = sc.nextLine();
        System.out.print("Monto: ");
        double monto = Double.parseDouble(sc.nextLine());
        System.out.print("Costo de aduana: ");
        double aduana = Double.parseDouble(sc.nextLine());
        System.out.print("Pais destino: ");
        String pais = sc.nextLine();

        Pedido p = new PedidoInternacional(id, cliente, monto, aduana, pais);

        try {
            gestor.agregarPedido(p);
            System.out.println("Pedido internacional agregado.");
        } catch (CapacidadExcedidaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void buscarPedido(Scanner sc, GestorPedidos gestor) {
        System.out.print("Id a buscar: ");
        int id = leerEntero(sc);

        try {
            Pedido p = gestor.buscarPorId(id);
            System.out.println(p);
        } catch (PedidoNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void cambiarEstado(Scanner sc, GestorPedidos gestor) {
        System.out.print("Id del pedido: ");
        int id = leerEntero(sc);

        EstadoPedido[] estados = EstadoPedido.values();
        System.out.println("Estados disponibles:");
        for (int i = 0; i < estados.length; i++) {
            System.out.println((i + 1) + ". " + estados[i]);
        }
        System.out.print("Nuevo estado (numero): ");
        int op = leerEntero(sc);

        if (op < 1 || op > estados.length) {
            System.out.println("La opcion que eligio es invalida");
            return;
        }

        try {
            gestor.cambiarEstado(id, estados[op - 1]);
            System.out.println("Estado actualizado correctamente.");
        } catch (PedidoNoEncontradoException | TransicionEstadoInvalidaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listarPorEstado(Scanner sc, GestorPedidos gestor) {
        EstadoPedido[] estados = EstadoPedido.values();
        System.out.println("Estados disponibles:");
        for (int i = 0; i < estados.length; i++) {
            System.out.println((i + 1) + ". " + estados[i]);
        }
        System.out.print("Elegi un estado (numero): ");
        int op = leerEntero(sc);

        if (op < 1 || op > estados.length) {
            System.out.println("Esa opcion no es valida");
            return;
        }

        Pedido[] resultado = gestor.listarPorEstado(estados[op - 1]);

        if (resultado.length == 0) {
            System.out.println("No hay pedidos en ese estado.");
        } else {
            for (Pedido p : resultado) {
                System.out.println(p);
            }
        }
    }

    private static void listarTodos(GestorPedidos gestor) {
        Pedido[] todos = gestor.listarTodos();

        if (todos.length == 0) {
            System.out.println("No hay pedidos registrados en este momento.");
            return;
        }

        for (Pedido p : todos) {
            System.out.println(p);
        }
    }
}