package vista;

import datos.RepositorioJugadores;
import modelo.Jugador;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Font;

public class PantallaRanking extends JFrame {

    private final RepositorioJugadores repoJugadores;
    private final PantallaMenuPrincipal menuPrincipal;

    public PantallaRanking(RepositorioJugadores repoJugadores, PantallaMenuPrincipal menuPrincipal) {
        this.repoJugadores = repoJugadores;
        this.menuPrincipal = menuPrincipal;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setTitle("Ranking de jugadores");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Ranking de jugadores", SwingConstants.CENTER);
        titulo.setFont(new Font("Serif", Font.BOLD, 18));
        add(titulo, BorderLayout.NORTH);

        Jugador[] activos = ordenadosPorPuntos(repoJugadores.listarActivos());

        String[] columnas = {"Posicion", "Usuario", "Puntos"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        for (int i = 0; i < activos.length; i++) {
            modelo.addRow(new Object[]{i + 1, activos[i].getUsuario(), activos[i].getPuntos()});
        }

        JTable tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel panelBoton = new JPanel();
        JButton botonVolver = new JButton("Volver");
        botonVolver.addActionListener(e -> volver());
        panelBoton.add(botonVolver);
        add(panelBoton, BorderLayout.SOUTH);
    }

    // ordena de mayor a menor puntos (burbuja simple, no hace falta nada mas sofisticado)
    private Jugador[] ordenadosPorPuntos(Jugador[] jugadores) {
        Jugador[] copia = jugadores.clone();
        for (int i = 0; i < copia.length - 1; i++) {
            for (int j = 0; j < copia.length - 1 - i; j++) {
                if (copia[j].getPuntos() < copia[j + 1].getPuntos()) {
                    Jugador temp = copia[j];
                    copia[j] = copia[j + 1];
                    copia[j + 1] = temp;
                }
            }
        }
        return copia;
    }

    private void volver() {
        menuPrincipal.setVisible(true);
        this.dispose();
    }
}
