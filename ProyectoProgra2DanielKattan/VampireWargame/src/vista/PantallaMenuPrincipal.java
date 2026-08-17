package vista;

import datos.RepositorioHistorial;
import datos.RepositorioJugadores;
import logica.ControladorPartida;
import modelo.Jugador;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class PantallaMenuPrincipal extends JFrame {

    private final Jugador jugadorActivo;
    private final RepositorioJugadores repoJugadores;
    private final RepositorioHistorial repoHistorial;

    public PantallaMenuPrincipal(Jugador jugadorActivo, RepositorioJugadores repoJugadores,
                                  RepositorioHistorial repoHistorial) {
        this.jugadorActivo = jugadorActivo;
        this.repoJugadores = repoJugadores;
        this.repoHistorial = repoHistorial;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setTitle("Vampire Wargame - Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 380);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Hola, " + jugadorActivo.getUsuario(), SwingConstants.CENTER);
        titulo.setFont(new Font("Serif", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(5, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        JButton botonJugar = new JButton("Jugar Vampire Wargame");
        JButton botonCuenta = new JButton("Mi cuenta");
        JButton botonRanking = new JButton("Ranking de jugadores");
        JButton botonHistorial = new JButton("Historial de mis ultimos juegos");
        JButton botonCerrarSesion = new JButton("Cerrar sesion");

        botonJugar.addActionListener(e -> iniciarNuevaPartida());
        botonCuenta.addActionListener(e -> abrirMiCuenta());
        botonRanking.addActionListener(e -> abrirRanking());
        botonHistorial.addActionListener(e -> abrirHistorial());
        botonCerrarSesion.addActionListener(e -> cerrarSesion());

        panelBotones.add(botonJugar);
        panelBotones.add(botonCuenta);
        panelBotones.add(botonRanking);
        panelBotones.add(botonHistorial);
        panelBotones.add(botonCerrarSesion);

        add(panelBotones, BorderLayout.CENTER);
    }

    private void iniciarNuevaPartida() {
        Jugador[] activos = repoJugadores.listarActivos();

        // armo la lista de oponentes posibles, sin incluirme a mi mismo
        Jugador[] oponentes = new Jugador[activos.length];
        int total = 0;
        for (Jugador j : activos) {
            if (!j.getUsuario().equalsIgnoreCase(jugadorActivo.getUsuario())) {
                oponentes[total] = j;
                total++;
            }
        }

        if (total == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay otro jugador registrado todavia para poder jugar.",
                    "Sin oponentes", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Jugador[] listaOponentes = new Jugador[total];
        System.arraycopy(oponentes, 0, listaOponentes, 0, total);

        Jugador oponente = (Jugador) JOptionPane.showInputDialog(this,
                "Elegi tu oponente:", "Nueva partida",
                JOptionPane.PLAIN_MESSAGE, null, listaOponentes, listaOponentes[0]);

        if (oponente == null) {
            return; // cancelo
        }

        ControladorPartida controlador = new ControladorPartida(jugadorActivo, oponente, repoJugadores, repoHistorial);
        PantallaTablero tablero = new PantallaTablero(controlador, this);
        tablero.setVisible(true);
        this.setVisible(false);
    }

    private void abrirMiCuenta() {
        PantallaMiCuenta miCuenta = new PantallaMiCuenta(jugadorActivo, this, repoJugadores, repoHistorial);
        miCuenta.setVisible(true);
        this.setVisible(false);
    }

    private void abrirRanking() {
        PantallaRanking ranking = new PantallaRanking(repoJugadores, this);
        ranking.setVisible(true);
        this.setVisible(false);
    }

    private void abrirHistorial() {
        PantallaHistorial historial = new PantallaHistorial(jugadorActivo, repoHistorial, this);
        historial.setVisible(true);
        this.setVisible(false);
    }

    private void cerrarSesion() {
        PantallaInicio inicio = new PantallaInicio(repoJugadores, repoHistorial);
        inicio.setVisible(true);
        this.dispose();
    }
}
