package logica;

import datos.RepositorioHistorial;
import datos.RepositorioJugadores;
import modelo.Bando;
import modelo.Jugador;
import modelo.Necromante;
import modelo.Pieza;
import modelo.RegistroPartida;
import modelo.TipoPieza;
import modelo.Vampiro;
import modelo.Zombie;

public class ControladorPartida {

    private final Tablero tablero;
    private final Jugador jugador1;
    private final Jugador jugador2;
    private final RepositorioJugadores repoJugadores;
    private final RepositorioHistorial repoHistorial;
    private final Ruleta ruleta;

    private Bando turnoActual;
    private int girosUsadosEnTurno;
    private TipoPieza tipoActual;
    private boolean partidaTerminada;
    private String mensajeFinal;

    // contador aparte, no lo calculamos desde el tablero porque invocar
    // zombies infla el conteo de piezas y desordena la resta
    private int piezasPerdidasBlanco;
    private int piezasPerdidasNegro;

    // jugador1 siempre arranca con blancas, jugador2 con negras
    public ControladorPartida(Jugador jugador1, Jugador jugador2,
                               RepositorioJugadores repoJugadores, RepositorioHistorial repoHistorial) {
        this.tablero = new Tablero();
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.repoJugadores = repoJugadores;
        this.repoHistorial = repoHistorial;
        this.ruleta = new Ruleta();
        this.turnoActual = Bando.BLANCO;
        this.girosUsadosEnTurno = 0;
        this.tipoActual = null;
        this.partidaTerminada = false;
        this.piezasPerdidasBlanco = 0;
        this.piezasPerdidasNegro = 0;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public Bando getTurnoActual() {
        return turnoActual;
    }

    public TipoPieza getTipoActual() {
        return tipoActual;
    }

    public boolean isPartidaTerminada() {
        return partidaTerminada;
    }

    public String getMensajeFinal() {
        return mensajeFinal;
    }

    public int getGirosUsadosEnTurno() {
        return girosUsadosEnTurno;
    }

    public Jugador getJugadorPorBando(Bando bando) {
        return bando == Bando.BLANCO ? jugador1 : jugador2;
    }

    private Bando bandoContrario(Bando bando) {
        return bando == Bando.BLANCO ? Bando.NEGRO : Bando.BLANCO;
    }

    public int getPiezasPerdidas(Bando bando) {
        return bando == Bando.BLANCO ? piezasPerdidasBlanco : piezasPerdidasNegro;
    }

    private void sumarPiezaPerdida(Bando bando) {
        if (bando == Bando.BLANCO) {
            piezasPerdidasBlanco++;
        } else {
            piezasPerdidasNegro++;
        }
    }

    // 0-1 piezas perdidas: 1 giro. 2-3 perdidas: 2 giros. 4+ perdidas: 3 giros.
    public int getGirosPermitidos(Bando bando) {
        int perdidas = getPiezasPerdidas(bando);
        if (perdidas >= 4) {
            return 3;
        }
        if (perdidas >= 2) {
            return 2;
        }
        return 1;
    }

    public boolean puedeVolverAGirar() {
        return girosUsadosEnTurno < getGirosPermitidos(turnoActual);
    }

    public TipoPieza girarRuleta() {
        girosUsadosEnTurno++;
        tipoActual = ruleta.girar();
        return tipoActual;
    }

    public boolean jugadorTieneTipo(Bando bando, TipoPieza tipo) {
        for (int f = 0; f < Tablero.DIMENSION; f++) {
            for (int c = 0; c < Tablero.DIMENSION; c++) {
                Pieza p = tablero.getPieza(f, c);
                if (p != null && p.getBando() == bando && p.getTipo() == tipo) {
                    return true;
                }
            }
        }
        return false;
    }

    public void pasarTurno() {
        turnoActual = bandoContrario(turnoActual);
        girosUsadosEnTurno = 0;
        tipoActual = null;
    }

    // se llama despues de que una accion (mover, invocar o atacar) se completo con exito
    public void finalizarAccion() {
        tipoActual = null;
    }

    public int distancia(Pieza pieza, int fila, int col) {
        return Math.max(Math.abs(pieza.getFila() - fila), Math.abs(pieza.getColumna() - col));
    }

    public boolean moverPieza(Pieza pieza, int filaDestino, int colDestino) {
        if (!tablero.estaVacia(filaDestino, colDestino)) {
            return false;
        }
        int dist = distancia(pieza, filaDestino, colDestino);
        if (dist == 0 || dist > pieza.getAlcanceMovimiento()) {
            return false;
        }
        tablero.colocarPieza(pieza.getFila(), pieza.getColumna(), null);
        tablero.colocarPieza(filaDestino, colDestino, pieza);
        return true;
    }

    // el necromante puede invocar en cualquier casilla vacia del tablero, sin limite de alcance
    public boolean invocarZombie(Necromante n, int fila, int col) {
        if (!tablero.estaVacia(fila, col)) {
            return false;
        }
        Zombie z = new Zombie(n.getBando(), fila, col);
        tablero.colocarPieza(fila, col, z);
        return true;
    }

    public Zombie buscarZombieAdyacente(Bando bando, int fila, int col) {
        for (int f = 0; f < Tablero.DIMENSION; f++) {
            for (int c = 0; c < Tablero.DIMENSION; c++) {
                Pieza p = tablero.getPieza(f, c);
                if (p instanceof Zombie && p.getBando() == bando) {
                    int dist = Math.max(Math.abs(f - fila), Math.abs(c - col));
                    if (dist == 1) {
                        return (Zombie) p;
                    }
                }
            }
        }
        return null;
    }

    // la lanza va en linea recta (horizontal o vertical), a distancia exacta de 2 y sin nada en medio
    public boolean lanzaGeometriaValida(Necromante n, int filaDestino, int colDestino) {
        boolean mismaFila = n.getFila() == filaDestino;
        boolean mismaColumna = n.getColumna() == colDestino;
        if (!mismaFila && !mismaColumna) {
            return false;
        }
        if (distancia(n, filaDestino, colDestino) != 2) {
            return false;
        }
        int filaIntermedia = (n.getFila() + filaDestino) / 2;
        int colIntermedia = (n.getColumna() + colDestino) / 2;
        return tablero.estaVacia(filaIntermedia, colIntermedia);
    }

    public String atacarNormal(Pieza atacante, Pieza defensor) {
        if (distancia(atacante, defensor.getFila(), defensor.getColumna()) != 1) {
            return null;
        }
        defensor.recibirDanio(atacante.getAtaque());
        return construirMensajeAtaque(atacante.getAtaque(), defensor);
    }

    public String absorbeSangre(Vampiro v, Pieza defensor) {
        if (distancia(v, defensor.getFila(), defensor.getColumna()) != 1) {
            return null;
        }
        defensor.recibirDanio(Vampiro.DANIO_ABSORCION);
        v.curar(Vampiro.DANIO_ABSORCION);
        return construirMensajeAtaque(Vampiro.DANIO_ABSORCION, defensor);
    }

    public String lanza(Necromante n, Pieza defensor) {
        if (!lanzaGeometriaValida(n, defensor.getFila(), defensor.getColumna())) {
            return null;
        }
        defensor.recibirDanioIgnorandoEscudo(Necromante.DANIO_LANZA);
        return construirMensajeAtaque(Necromante.DANIO_LANZA, defensor);
    }

    public String ataqueViaZombie(Zombie z, Pieza defensor) {
        int dist = Math.max(Math.abs(z.getFila() - defensor.getFila()), Math.abs(z.getColumna() - defensor.getColumna()));
        if (dist != 1) {
            return null;
        }
        defensor.recibirDanio(Necromante.DANIO_ATAQUE_VIA_ZOMBIE);
        return construirMensajeAtaque(Necromante.DANIO_ATAQUE_VIA_ZOMBIE, defensor);
    }

    private String construirMensajeAtaque(int danio, Pieza defensor) {
        String nombreJugador = getJugadorPorBando(defensor.getBando()).getUsuario();
        if (defensor.estaDestruida()) {
            tablero.colocarPieza(defensor.getFila(), defensor.getColumna(), null);
            sumarPiezaPerdida(defensor.getBando());
            return "Se destruyo la pieza " + defensor.getNombre() + " del jugador " + nombreJugador + ".";
        }
        return "Se ataco la pieza " + defensor.getNombre() + " y se le quitaron " + danio
                + " puntos; le quedan " + defensor.getEscudo() + " puntos de escudo y "
                + defensor.getVida() + " de vida.";
    }

    // hay que llamarla despues de cada accion para ver si alguien se quedo sin piezas
    public boolean verificarFinDePartida() {
        if (partidaTerminada) {
            return true;
        }
        int blancas = tablero.contarPiezasDeBando(Bando.BLANCO);
        int negras = tablero.contarPiezasDeBando(Bando.NEGRO);
        if (blancas == 0 || negras == 0) {
            Bando bandoGanador = blancas == 0 ? Bando.NEGRO : Bando.BLANCO;
            Jugador ganador = getJugadorPorBando(bandoGanador);
            Jugador perdedor = getJugadorPorBando(bandoContrario(bandoGanador));
            finalizar(ganador, perdedor,
                    ganador.getUsuario() + " vencio a " + perdedor.getUsuario() + ". Felicidades, has ganado 3 puntos!");
        }
        return partidaTerminada;
    }

    public void retirarse(Bando bandoQueSeRetira) {
        Bando bandoGanador = bandoContrario(bandoQueSeRetira);
        Jugador ganador = getJugadorPorBando(bandoGanador);
        Jugador retirado = getJugadorPorBando(bandoQueSeRetira);
        finalizar(ganador, retirado,
                retirado.getUsuario() + " se ha retirado. Felicidades, " + ganador.getUsuario() + ", has ganado 3 puntos!");
    }

    private void finalizar(Jugador ganador, Jugador perdedor, String mensaje) {
        ganador.sumarPuntos(3);
        this.mensajeFinal = mensaje;
        this.partidaTerminada = true;
        repoHistorial.agregar(new RegistroPartida(jugador1.getUsuario(), jugador2.getUsuario(), mensaje));
    }
}
