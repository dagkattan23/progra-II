package vista;

import datos.RepositorioHistorial;
import modelo.Jugador;
import modelo.RegistroPartida;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;

public class PantallaHistorial extends JFrame {

    private final Jugador jugador;
    private final RepositorioHistorial repoHistorial;
    private final PantallaMenuPrincipal menuPrincipal;

    public PantallaHistorial(Jugador jugador, RepositorioHistorial repoHistorial,
                              PantallaMenuPrincipal menuPrincipal) {
        this.jugador = jugador;
        this.repoHistorial = repoHistorial;
        this.menuPrincipal = menuPrincipal;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setTitle("Historial de mis ultimos juegos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Historial de mis ultimos juegos", SwingConstants.CENTER);
        titulo.setFont(new Font("Serif", Font.BOLD, 16));
        add(titulo, BorderLayout.NORTH);

        RegistroPartida[] registros = repoHistorial.listarPorJugador(jugador.getUsuario());
        String[] filas = new String[registros.length];
        for (int i = 0; i < registros.length; i++) {
            filas[i] = "[" + registros[i].getFechaFormateada() + "] " + registros[i].getMensaje();
        }
        if (filas.length == 0) {
            filas = new String[]{"Todavia no jugaste ninguna partida."};
        }

        JList<String> lista = new JList<>(filas);
        add(new JScrollPane(lista), BorderLayout.CENTER);

        JPanel panelBoton = new JPanel();
        JButton botonVolver = new JButton("Volver");
        botonVolver.addActionListener(e -> volver());
        panelBoton.add(botonVolver);
        add(panelBoton, BorderLayout.SOUTH);
    }

    private void volver() {
        menuPrincipal.setVisible(true);
        this.dispose();
    }
}
