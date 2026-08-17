package modelo;

// clase base para todas las piezas del juego
public abstract class Pieza {

    private int ataque;
    private int vida;
    private int escudo;
    private final Bando bando;
    private int fila;
    private int columna;

    protected Pieza(int ataque, int vida, int escudo, Bando bando, int fila, int columna) {
        this.ataque = ataque;
        this.vida = vida;
        this.escudo = escudo;
        this.bando = bando;
        this.fila = fila;
        this.columna = columna;
    }

    // cuantas casillas se puede mover, cada pieza lo define distinto
    public abstract int getAlcanceMovimiento();

    // texto que se dibuja en la casilla
    public abstract String getSimbolo();

    public abstract String getNombre();

    // para saber que tipo es sin andar comparando clases por todos lados
    public abstract TipoPieza getTipo();

    // primero se gasta el escudo y despues la vida
    public final void recibirDanio(int cantidad) {
        if (cantidad <= 0) {
            return;
        }
        if (this.escudo > 0) {
            int absorbido = Math.min(this.escudo, cantidad);
            this.escudo -= absorbido;
            cantidad -= absorbido;
        }
        if (cantidad > 0) {
            this.vida = Math.max(0, this.vida - cantidad);
        }
    }

    // esta ignora el escudo (la usa la lanza del necromante)
    public final void recibirDanioIgnorandoEscudo(int cantidad) {
        this.vida = Math.max(0, this.vida - cantidad);
    }

    public void curar(int cantidad) {
        this.vida += cantidad;
    }

    public boolean estaDestruida() {
        return this.vida <= 0;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getVida() {
        return vida;
    }

    public int getEscudo() {
        return escudo;
    }

    public Bando getBando() {
        return bando;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }
}
