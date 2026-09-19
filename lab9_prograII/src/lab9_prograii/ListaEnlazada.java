package lab9_prograii;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Predicate;


public class ListaEnlazada<T> {

    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamaño;
    private final int capacidadMaxima;

    public ListaEnlazada(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
        this.cabeza = null;
        this.cola = null;
        this.tamaño = 0;
    }

    private void insertarAlFinal(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.setSiguiente(nuevo);
            cola = nuevo;
        }
        tamaño++;
    }

    private T quitarCabeza() {
        T dato = cabeza.getDato();
        cabeza = cabeza.getSiguiente();
        if (cabeza == null) {
            cola = null;
        }
        tamaño--;
        return dato;
    }

    public synchronized void agregar(T dato) throws InterruptedException {
        while (tamaño >= capacidadMaxima) {
            wait();
        }
        insertarAlFinal(dato);
        notifyAll();
    }

    public synchronized void agregarForzado(T dato) {
        insertarAlFinal(dato);
        notifyAll();
    }

    
    public synchronized void agregarDescartandoAntiguo(T dato) {
        if (tamaño >= capacidadMaxima && cabeza != null) {
            quitarCabeza();
        }
        insertarAlFinal(dato);
        notifyAll();
    }

   
    public synchronized T extraerPrimero() throws InterruptedException {
        while (tamaño == 0) {
            wait();
        }
        T dato = quitarCabeza();
        notifyAll();
        return dato;
    }

    /** Extrae el primer elemento si existe. Devuelve null si está vacía (no espera). */
    public synchronized T extraerSiHay() {
        if (tamaño == 0) {
            return null;
        }
        T dato = quitarCabeza();
        notifyAll();
        return dato;
    }

    
    public synchronized T extraerPrimeroQueCumpla(Predicate<T> criterio) {
        Nodo<T> actual = cabeza;
        Nodo<T> anterior = null;
        while (actual != null) {
            if (criterio.test(actual.getDato())) {
                if (anterior == null) {
                    cabeza = actual.getSiguiente();
                } else {
                    anterior.setSiguiente(actual.getSiguiente());
                }
                if (actual == cola) {
                    cola = anterior;
                }
                tamaño--;
                notifyAll();
                return actual.getDato();
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
        return null;
    }

    private T quitarMejor(Predicate<T> filtro, Comparator<T> comparador) {
        Nodo<T> actual = cabeza;
        Nodo<T> anterior = null;
        Nodo<T> mejor = null;
        Nodo<T> mejorAnterior = null;

        while (actual != null) {
            if (filtro.test(actual.getDato())) {
                if (mejor == null || comparador.compare(actual.getDato(), mejor.getDato()) < 0) {
                    mejor = actual;
                    mejorAnterior = anterior;
                }
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }

        if (mejor == null) {
            return null;
        }
        if (mejorAnterior == null) {
            cabeza = mejor.getSiguiente();
        } else {
            mejorAnterior.setSiguiente(mejor.getSiguiente());
        }
        if (mejor == cola) {
            cola = mejorAnterior;
        }
        tamaño--;
        notifyAll();
        return mejor.getDato();
    }

   
    public synchronized T extraerMayorPrioridad(Comparator<T> comparador) throws InterruptedException {
        while (tamaño == 0) {
            wait();
        }
        return quitarMejor(dato -> true, comparador);
    }

    
    public synchronized T extraerMayorPrioridadQueCumpla(Predicate<T> filtro, Comparator<T> comparador) {
        return quitarMejor(filtro, comparador);
    }

    
    public synchronized boolean eliminar(T dato) {
        return extraerPrimeroQueCumpla(d -> d.equals(dato)) != null;
    }

    
    public synchronized T buscar(Predicate<T> criterio) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (criterio.test(actual.getDato())) {
                return actual.getDato();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    
    public synchronized T obtener(int indice) {
        if (indice < 0 || indice >= tamaño) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }

        public synchronized void recorrer(Consumer<T> accion) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            accion.accept(actual.getDato());
            actual = actual.getSiguiente();
        }
    }

    
    public synchronized int contarQueCumplen(Predicate<T> criterio) {
        int contador = 0;
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (criterio.test(actual.getDato())) {
                contador++;
            }
            actual = actual.getSiguiente();
        }
        return contador;
    }

    public synchronized int tamaño() {
        return tamaño;
    }

    public synchronized boolean estaVacia() {
        return tamaño == 0;
    }

    public synchronized boolean estaLlena() {
        return tamaño >= capacidadMaxima;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    
    public synchronized void vaciar() {
        cabeza = null;
        cola = null;
        tamaño = 0;
        notifyAll();
    }
}