package modelo;

// no hereda nadie de esta, por eso es final
public final class Zombie extends Pieza {

    public static final int ATAQUE_BASE = 1;
    public static final int VIDA_BASE = 1;
    public static final int ESCUDO_BASE = 0;

    public Zombie(Bando bando, int fila, int columna) {
        super(ATAQUE_BASE, VIDA_BASE, ESCUDO_BASE, bando, fila, columna);
    }

    @Override
    public int getAlcanceMovimiento() {
        return 0; // no se mueve solo
    }

    @Override
    public String getSimbolo() {
        return "Z";
    }

    @Override
    public String getNombre() {
        return "Zombie";
    }

    @Override
    public TipoPieza getTipo() {
        return TipoPieza.ZOMBIE;
    }
}
