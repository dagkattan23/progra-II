package vista;

import datos.RepositorioHistorial;
import datos.RepositorioJugadores;
import excepciones.PasswordInvalidaException;
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

public class PantallaMiCuenta extends JFrame {

    private static final int LONGITUD_CONTRASENIA = 5;

    private final Jugador jugador;
    private final PantallaMenuPrincipal menuPrincipal;
    private final RepositorioJugadores repoJugadores;
    private final RepositorioHistorial repoHistorial;

    public PantallaMiCuenta(Jugador jugador, PantallaMenuPrincipal menuPrincipal,
                             RepositorioJugadores repoJugadores, RepositorioHistorial repoHistorial) {
        this.jugador = jugador;
        this.menuPrincipal = menuPrincipal;
        this.repoJugadores = repoJugadores;
        this.repoHistorial = repoHistorial;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setTitle("Mi cuenta");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(380, 320);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Mi cuenta", SwingConstants.CENTER);
        titulo.setFont(new Font("Serif", Font.BOLD, 20));
        add(titulo, BorderLayout.NORTH);

        JPanel panelInfo = new JPanel(new GridLayout(3, 2, 5, 10));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panelInfo.add(new JLabel("Usuario:"));
        panelInfo.add(new JLabel(jugador.getUsuario()));
        panelInfo.add(new JLabel("Puntos:"));
        panelInfo.add(new JLabel(String.valueOf(jugador.getPuntos())));
        panelInfo.add(new JLabel("Fecha de ingreso:"));
        panelInfo.add(new JLabel(jugador.getFechaIngresoFormateada()));
        add(panelInfo, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 5, 5));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));

        JButton botonCambiar = new JButton("Cambiar contrasena");
        JButton botonCerrarCuenta = new JButton("Cerrar mi cuenta");
        JButton botonVolver = new JButton("Volver");

        botonCambiar.addActionListener(e -> cambiarContrasenia());
        botonCerrarCuenta.addActionListener(e -> cerrarCuenta());
        botonVolver.addActionListener(e -> volver());

        panelBotones.add(botonCambiar);
        panelBotones.add(botonCerrarCuenta);
        panelBotones.add(botonVolver);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cambiarContrasenia() {
        String nueva = JOptionPane.showInputDialog(this, "Contrasena nueva (5 caracteres):");
        if (nueva == null) {
            return; // cancelo
        }
        try {
            if (nueva.length() != LONGITUD_CONTRASENIA) {
                throw new PasswordInvalidaException();
            }
            jugador.setContrasenia(nueva);
            JOptionPane.showMessageDialog(this, "Contrasena actualizada.");
        } catch (PasswordInvalidaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cerrarCuenta() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "Seguro que queres cerrar tu cuenta? No vas a poder iniciar sesion de nuevo.",
                "Cerrar cuenta", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            jugador.setActivo(false);
            JOptionPane.showMessageDialog(this, "Tu cuenta fue cerrada.");
            PantallaInicio inicio = new PantallaInicio(repoJugadores, repoHistorial);
            inicio.setVisible(true);
            this.dispose();
            menuPrincipal.dispose();
        }
    }

    private void volver() {
        menuPrincipal.setVisible(true);
        this.dispose();
    }
}
