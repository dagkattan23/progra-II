package modelo;

public class Necromante extends Pieza {

    public static final int ATAQUE_BASE = 4;
    public static final int VIDA_BASE = 3;
    public static final int ESCUDO_BASE = 2;

    public static final int DANIO_LANZA = 2; // ignora escudo, alcance 2 casillas
    public static final int DANIO_ATAQUE_VIA_ZOMBIE = 1;

    public Necromante(Bando bando, int fila, int columna) {
        super(ATAQUE_BASE, VIDA_BASE, ESCUDO_BASE, bando, fila, columna);
    }

    @Override
    public int getAlcanceMovimiento() {
        return 1;
    }

    @Override
    public String getSimbolo() {
        return "N";
    }

    @Override
    public String getNombre() {
        return "Necromante";
    }

    @Override
    public TipoPieza getTipo() {
        return TipoPieza.NECROMANTE;
    }
}
