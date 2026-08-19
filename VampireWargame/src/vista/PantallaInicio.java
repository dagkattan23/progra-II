package vista; 

import datos.RepositorioHistorial;
import datos.RepositorioJugadores;
import excepciones.CredencialesInvalidasException;
import modelo.Jugador;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class PantallaInicio extends JFrame {

    private final RepositorioJugadores repoJugadores;
    private final RepositorioHistorial repoHistorial;

    private JTextField campoUsuario;
    private JPasswordField campoContrasenia;

    public PantallaInicio(RepositorioJugadores repoJugadores, RepositorioHistorial repoHistorial) {
        this.repoJugadores = repoJugadores;
        this.repoHistorial = repoHistorial;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setTitle("Vampire Wargame");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("VAMPIRE WARGAME", SwingConstants.CENTER);
        titulo.setFont(new Font("Serif", Font.BOLD, 22));
        add(titulo, BorderLayout.NORTH);

        JPanel panelCentro = new JPanel(new GridLayout(2, 2, 5, 10));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        panelCentro.add(new JLabel("Usuario:"));
        campoUsuario = new JTextField();
        panelCentro.add(campoUsuario);

        panelCentro.add(new JLabel("Contrasena:"));
        campoContrasenia = new JPasswordField();
        panelCentro.add(campoContrasenia);

        add(panelCentro, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton botonIniciarSesion = new JButton("Iniciar sesion");
        JButton botonCrearJugador = new JButton("Crear jugador");
        JButton botonSalir = new JButton("Salir");

        botonIniciarSesion.addActionListener(e -> iniciarSesion());
        botonCrearJugador.addActionListener(e -> abrirPantallaRegistro());
        botonSalir.addActionListener(e -> System.exit(0));

        panelBotones.add(botonIniciarSesion);
        panelBotones.add(botonCrearJugador);
        panelBotones.add(botonSalir);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private void iniciarSesion() {
        String usuario = campoUsuario.getText().trim();
        String contrasenia = new String(campoContrasenia.getPassword());

        try {
            Jugador jugador = validarCredenciales(usuario, contrasenia);
            abrirMenuPrincipal(jugador);
        } catch (CredencialesInvalidasException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error de inicio de sesion", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Jugador validarCredenciales(String usuario, String contrasenia)
            throws CredencialesInvalidasException {
        Jugador jugador = repoJugadores.buscarPorUsuario(usuario);
        if (jugador == null || !jugador.isActivo() || !jugador.getContrasenia().equals(contrasenia)) {
            throw new CredencialesInvalidasException();
        }
        return jugador;
    }

    private void abrirPantallaRegistro() {
        PantallaRegistro registro = new PantallaRegistro(repoJugadores, repoHistorial, this);
        registro.setVisible(true);
        this.setVisible(false);
    }

    private void abrirMenuPrincipal(Jugador jugador) {
        PantallaMenuPrincipal menu = new PantallaMenuPrincipal(jugador, repoJugadores, repoHistorial);
        menu.setVisible(true);
        this.dispose();
    }
}
