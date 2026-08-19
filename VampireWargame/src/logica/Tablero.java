package logica;

import modelo.Bando;
import modelo.HombreLobo;
import modelo.Necromante;
import modelo.Pieza;
import modelo.Vampiro;

public class Tablero {

    public static final int DIMENSION = 6;

    private final Pieza[][] casillas;

    public Tablero() {
        this.casillas = new Pieza[DIMENSION][DIMENSION];
        inicializarPiezas();
    }

    private void inicializarPiezas() {
        int filaNegras = 0;
        int filaBlancas = DIMENSION - 1;

        colocarFilaInicial(filaNegras, Bando.NEGRO);
        colocarFilaInicial(filaBlancas, Bando.BLANCO);
    }

    // orden: hombre lobo, vampiro, muerte, muerte, vampiro, hombre lobo
    private void colocarFilaInicial(int fila, Bando bando) {
        casillas[fila][0] = new HombreLobo(bando, fila, 0);
        casillas[fila][1] = new Vampiro(bando, fila, 1);
        casillas[fila][2] = new Necromante(bando, fila, 2);
        casillas[fila][3] = new Necromante(bando, fila, 3);
        casillas[fila][4] = new Vampiro(bando, fila, 4);
        casillas[fila][5] = new HombreLobo(bando, fila, 5);
    }

    public Pieza getPieza(int fila, int columna) {
        return casillas[fila][columna];
    }

    public void colocarPieza(int fila, int columna, Pieza pieza) {
        casillas[fila][columna] = pieza;
        if (pieza != null) {
            pieza.setFila(fila);
            pieza.setColumna(columna);
        }
    }

    public boolean estaVacia(int fila, int columna) {
        return casillas[fila][columna] == null;
    }

    // cuenta recursivamente cuantas piezas le quedan a un bando
    public int contarPiezasDeBando(Bando bando) {
        return contarPiezasDeBandoRecursivo(bando, 0, 0);
    }

    private int contarPiezasDeBandoRecursivo(Bando bando, int fila, int columna) {
        if (fila >= DIMENSION) {
            return 0;
        }

        int siguienteFila = (columna == DIMENSION - 1) ? fila + 1 : fila;
        int siguienteColumna = (columna == DIMENSION - 1) ? 0 : columna + 1;

        int cuentaActual = 0;
        Pieza pieza = casillas[fila][columna];
        if (pieza != null && pieza.getBando() == bando) {
            cuentaActual = 1;
        }

        return cuentaActual + contarPiezasDeBandoRecursivo(bando, siguienteFila, siguienteColumna);
    }
}
