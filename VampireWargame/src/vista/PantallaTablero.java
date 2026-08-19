package vista;

import logica.ControladorPartida;
import logica.Tablero;
import modelo.Bando;
import modelo.Necromante;
import modelo.Pieza;
import modelo.TipoPieza;
import modelo.Vampiro;
import modelo.Zombie;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PantallaTablero extends JFrame {

    private final ControladorPartida controlador;
    private final PantallaMenuPrincipal menuPrincipal;

    private JLabel[][] etiquetas;
    private JLabel labelTurno;
    private JLabel labelRuleta;
    private JButton botonGirar;

    private int filaOrigen = -1;
    private int colOrigen = -1;

    public PantallaTablero(ControladorPartida controlador, PantallaMenuPrincipal menuPrincipal) {
        this.controlador = controlador;
        this.menuPrincipal = menuPrincipal;
        construirInterfaz();
        actualizarEncabezado();
    }

    private void construirInterfaz() {
        setTitle("Vampire Wargame - Tablero");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(560, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new GridLayout(2, 1));
        labelTurno = new JLabel("", SwingConstants.CENTER);
        labelTurno.setFont(new Font("Serif", Font.BOLD, 16));
        labelRuleta = new JLabel("", SwingConstants.CENTER);
        panelSuperior.add(labelTurno);
        panelSuperior.add(labelRuleta);
        add(panelSuperior, BorderLayout.NORTH);

        JPanel panelTablero = new JPanel(new GridLayout(Tablero.DIMENSION, Tablero.DIMENSION));
        panelTablero.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        etiquetas = new JLabel[Tablero.DIMENSION][Tablero.DIMENSION];
        for (int fila = 0; fila < Tablero.DIMENSION; fila++) {
            for (int columna = 0; columna < Tablero.DIMENSION; columna++) {
                JLabel casilla = crearCasilla(fila, columna);
                etiquetas[fila][columna] = casilla;
                panelTablero.add(casilla);
            }
        }
        // recien aca el arreglo etiquetas esta completo, ahora si podemos pintar todo
        actualizarTableroVisual();
        add(panelTablero, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new GridLayout(1, 2, 10, 10));
        botonGirar = new JButton("Girar ruleta");
        botonGirar.addActionListener(e -> onGirarRuleta());
        JButton botonRetirarse = new JButton("Retirarse de la partida");
        botonRetirarse.addActionListener(e -> onRetirarse());
        panelInferior.add(botonGirar);
        panelInferior.add(botonRetirarse);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private JLabel crearCasilla(int fila, int columna) {
        JLabel casilla = new JLabel("", SwingConstants.CENTER);
        casilla.setOpaque(true);
        casilla.setFont(new Font("SansSerif", Font.BOLD, 14));
        casilla.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        casilla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClickCasilla(fila, columna);
            }
        });

        return casilla;
    }

    private void pintarCasilla(int fila, int columna) {
        JLabel casilla = etiquetas[fila][columna];
        boolean esClaro = (fila + columna) % 2 == 0;
        boolean esSeleccionada = (fila == filaOrigen && columna == colOrigen);

        casilla.setBackground(esSeleccionada
                ? new Color(90, 150, 90)
                : (esClaro ? new Color(222, 202, 176) : new Color(120, 84, 60)));

        Pieza pieza = controlador.getTablero().getPieza(fila, columna);
        if (pieza != null) {
            casilla.setText(pieza.getSimbolo());
            casilla.setForeground(pieza.getBando() == Bando.BLANCO
                    ? new Color(255, 215, 0)
                    : new Color(178, 34, 34));
            casilla.setToolTipText(pieza.getNombre() + " (" + pieza.getBando() + ") - vida:"
                    + pieza.getVida() + " escudo:" + pieza.getEscudo());
        } else {
            casilla.setText("");
            casilla.setToolTipText(null);
        }
    }

    private void actualizarTableroVisual() {
        for (int f = 0; f < Tablero.DIMENSION; f++) {
            for (int c = 0; c < Tablero.DIMENSION; c++) {
                pintarCasilla(f, c);
            }
        }
    }

    private void actualizarEncabezado() {
        Bando turno = controlador.getTurnoActual();
        labelTurno.setText("Turno de: " + controlador.getJugadorPorBando(turno).getUsuario() + " (" + turno + ")");
        if (controlador.getTipoActual() == null) {
            labelRuleta.setText("Gira la ruleta para poder jugar");
        } else {
            labelRuleta.setText("Te toca mover: " + controlador.getTipoActual());
        }
    }

    private void onGirarRuleta() {
        TipoPieza tipo = controlador.girarRuleta();
        actualizarEncabezado();

        if (!controlador.jugadorTieneTipo(controlador.getTurnoActual(), tipo)) {
            if (controlador.puedeVolverAGirar()) {
                JOptionPane.showMessageDialog(this,
                        "No tenes piezas de tipo " + tipo + ". Volve a girar (te quedan giros).");
            } else {
                JOptionPane.showMessageDialog(this,
                        "No tenes piezas de tipo " + tipo + " y se te acabaron los giros. Perdiste el turno.");
                controlador.pasarTurno();
                actualizarEncabezado();
            }
        } else {
            botonGirar.setEnabled(false);
        }
    }

    private void onClickCasilla(int fila, int columna) {
        if (controlador.getTipoActual() == null) {
            JOptionPane.showMessageDialog(this, "Primero tenes que girar la ruleta.");
            return;
        }

        if (filaOrigen == -1) {
            seleccionarOrigen(fila, columna);
            return;
        }

        if (fila == filaOrigen && columna == colOrigen) {
            limpiarSeleccion();
            actualizarTableroVisual();
            return;
        }

        Pieza origenPieza = controlador.getTablero().getPieza(filaOrigen, colOrigen);
        Pieza destinoPieza = controlador.getTablero().getPieza(fila, columna);

        boolean accionCompletada;
        if (destinoPieza == null) {
            accionCompletada = manejarDestinoVacio(origenPieza, fila, columna);
        } else if (destinoPieza.getBando() == controlador.getTurnoActual()) {
            JOptionPane.showMessageDialog(this, "Esa pieza es tuya, elegi otra casilla.");
            accionCompletada = false;
        } else {
            accionCompletada = manejarAtaque(origenPieza, destinoPieza);
        }

        if (accionCompletada) {
            limpiarSeleccion();
            actualizarTableroVisual();
            controlador.finalizarAccion();

            if (controlador.verificarFinDePartida()) {
                mostrarFinDePartida();
                return;
            }

            continuarTurno();
        } else {
            actualizarTableroVisual();
        }
    }

    private void seleccionarOrigen(int fila, int columna) {
        Pieza pieza = controlador.getTablero().getPieza(fila, columna);
        if (pieza == null || pieza.getBando() != controlador.getTurnoActual()
                || pieza.getTipo() != controlador.getTipoActual()) {
            JOptionPane.showMessageDialog(this, "Selecciona una pieza tuya del tipo que salio en la ruleta.");
            return;
        }
        filaOrigen = fila;
        colOrigen = columna;
        actualizarTableroVisual();
    }

    private void limpiarSeleccion() {
        filaOrigen = -1;
        colOrigen = -1;
    }

    private boolean manejarDestinoVacio(Pieza origenPieza, int fila, int columna) {
        if (origenPieza instanceof Necromante) {
            Necromante n = (Necromante) origenPieza;
            boolean puedeMover = controlador.distancia(n, fila, columna) <= n.getAlcanceMovimiento();

            String[] opciones = puedeMover
                    ? new String[]{"Mover aqui", "Invocar Zombie aqui", "Cancelar"}
                    : new String[]{"Invocar Zombie aqui", "Cancelar"};

            int eleccion = JOptionPane.showOptionDialog(this,
                    "Que queres hacer con el Necromante?", "Necromante",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, opciones, opciones[0]);

            if (eleccion < 0 || opciones[eleccion].equals("Cancelar")) {
                return false;
            }
            if (opciones[eleccion].equals("Mover aqui")) {
                return controlador.moverPieza(n, fila, columna);
            }
            return controlador.invocarZombie(n, fila, columna);
        }

        boolean ok = controlador.moverPieza(origenPieza, fila, columna);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Movimiento invalido, esa casilla esta fuera de alcance.");
        }
        return ok;
    }

    private boolean manejarAtaque(Pieza origenPieza, Pieza defensor) {
        int distancia = controlador.distancia(origenPieza, defensor.getFila(), defensor.getColumna());

        List<String> opcionesList = new ArrayList<>();
        if (distancia == 1) {
            opcionesList.add("Ataque normal");
        }
        if (origenPieza instanceof Vampiro && distancia == 1) {
            opcionesList.add("Absorcion de sangre");
        }

        Necromante necro = (origenPieza instanceof Necromante) ? (Necromante) origenPieza : null;
        Zombie zombiePropio = null;
        if (necro != null) {
            if (controlador.lanzaGeometriaValida(necro, defensor.getFila(), defensor.getColumna())) {
                opcionesList.add("Lanza (ignora escudo)");
            }
            if (distancia > 2) {
                zombiePropio = controlador.buscarZombieAdyacente(necro.getBando(), defensor.getFila(), defensor.getColumna());
                if (zombiePropio != null) {
                    opcionesList.add("Ataque a traves de Zombie");
                }
            }
        }

        if (opcionesList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay ninguna accion valida contra esa pieza desde ahi.");
            return false;
        }

        opcionesList.add("Cancelar");
        String[] opciones = opcionesList.toArray(new String[0]);

        int eleccion = JOptionPane.showOptionDialog(this,
                "Que tipo de ataque queres hacer?", "Atacar",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);

        if (eleccion < 0 || opciones[eleccion].equals("Cancelar")) {
            return false;
        }

        String resultado;
        String elegido = opciones[eleccion];
        if (elegido.equals("Ataque normal")) {
            resultado = controlador.atacarNormal(origenPieza, defensor);
        } else if (elegido.equals("Absorcion de sangre")) {
            resultado = controlador.absorbeSangre((Vampiro) origenPieza, defensor);
        } else if (elegido.equals("Lanza (ignora escudo)")) {
            resultado = controlador.lanza(necro, defensor);
        } else {
            resultado = controlador.ataqueViaZombie(zombiePropio, defensor);
        }

        if (resultado == null) {
            JOptionPane.showMessageDialog(this, "Esa accion ya no es valida, elegi otra.");
            return false;
        }

        JOptionPane.showMessageDialog(this, resultado);
        return true;
    }

    private void continuarTurno() {
        if (controlador.puedeVolverAGirar()) {
            int opcion = JOptionPane.showConfirmDialog(this,
                    "Te quedan giros en este turno (" + controlador.getGirosUsadosEnTurno()
                            + "/" + controlador.getGirosPermitidos(controlador.getTurnoActual())
                            + "). Queres volver a girar?",
                    "Giro extra", JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                actualizarEncabezado();
                botonGirar.setEnabled(true);
                return;
            }
        }
        controlador.pasarTurno();
        actualizarEncabezado();
        botonGirar.setEnabled(true);
    }

    private void onRetirarse() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "Seguro que te queres retirar de la partida?",
                "Confirmar retiro", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            controlador.retirarse(controlador.getTurnoActual());
            mostrarFinDePartida();
        }
    }

    private void mostrarFinDePartida() {
        JOptionPane.showMessageDialog(this, controlador.getMensajeFinal());
        menuPrincipal.setVisible(true);
        this.dispose();
    }
}
