package modelo;

public class HombreLobo extends Pieza {

    public static final int ATAQUE_BASE = 5;
    public static final int VIDA_BASE = 5;
    public static final int ESCUDO_BASE = 2;

    public HombreLobo(Bando bando, int fila, int columna) {
        super(ATAQUE_BASE, VIDA_BASE, ESCUDO_BASE, bando, fila, columna);
    }

    @Override
    public int getAlcanceMovimiento() {
        return 2; // se puede mover hasta 2 casillas
    }

    @Override
    public String getSimbolo() {
        return "HL";
    }

    @Override
    public String getNombre() {
        return "Hombre Lobo";
    }

    @Override
    public TipoPieza getTipo() {
        return TipoPieza.HOMBRE_LOBO;
    }
}
