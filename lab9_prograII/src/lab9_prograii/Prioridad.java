package lab9_prograii;

public enum Prioridad {
    BAJA(1),
    NORMAL(2),
    ALTA(3),
    URGENTE(4);

    private final int nivel;

    Prioridad(int nivel) {
        this.nivel = nivel;
    }

    public int getNivel() {
        return nivel;
    }
}