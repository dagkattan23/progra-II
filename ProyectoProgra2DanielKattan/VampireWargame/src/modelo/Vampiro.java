package modelo;

public class Vampiro extends Pieza {

    public static final int ATAQUE_BASE = 3;
    public static final int VIDA_BASE = 4;
    public static final int ESCUDO_BASE = 5;

    // dano y curacion de la habilidad de absorcion de sangre
    public static final int DANIO_ABSORCION = 1;

    public Vampiro(Bando bando, int fila, int columna) {
        super(ATAQUE_BASE, VIDA_BASE, ESCUDO_BASE, bando, fila, columna);
    }

    @Override
    public int getAlcanceMovimiento() {
        return 1;
    }

    @Override
    public String getSimbolo() {
        return "V";
    }

    @Override
    public String getNombre() {
        return "Vampiro";
    }

    @Override
    public TipoPieza getTipo() {
        return TipoPieza.VAMPIRO;
    }
}
