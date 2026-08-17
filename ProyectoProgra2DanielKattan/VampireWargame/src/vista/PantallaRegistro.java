package vista;

import datos.RepositorioHistorial;
import datos.RepositorioJugadores;
import excepciones.PasswordInvalidaException;
import excepciones.UsuarioDuplicadoException;
import modelo.Jugador;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PantallaRegistro extends JFrame {

    private static final int LONGITUD_CONTRASENIA = 5;

    private final RepositorioJugadores repoJugadores;
    private final RepositorioHistorial repoHistorial;
    private final PantallaInicio pantallaInicio;

    private JTextField campoUsuario;
    private JPasswordField campoContrasenia;

    public PantallaRegistro(RepositorioJugadores repoJugadores, RepositorioHistorial repoHistorial,
                             PantallaInicio pantallaInicio) {
        this.repoJugadores = repoJugadores;
        this.repoHistorial = repoHistorial;
        this.pantallaInicio = pantallaInicio;
        construirInterfaz();
    }

    private void construirInterfaz() {
        setTitle("Crear jugador");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(380, 250);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelCentro = new JPanel(new GridLayout(2, 2, 5, 10));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        panelCentro.add(new JLabel("Usuario:"));
        campoUsuario = new JTextField();
        panelCentro.add(campoUsuario);

        panelCentro.add(new JLabel("Contrasena (5 caracteres):"));
        campoContrasenia = new JPasswordField();
        panelCentro.add(campoContrasenia);

        add(panelCentro, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton botonRegistrar = new JButton("Registrar");
        JButton botonCancelar = new JButton("Cancelar");

        botonRegistrar.addActionListener(e -> registrarJugador());
        botonCancelar.addActionListener(e -> volverAInicio());

        panelBotones.add(botonRegistrar);
        panelBotones.add(botonCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                volverAInicio();
            }
        });
    }

    private void registrarJugador() {
        String usuario = campoUsuario.getText().trim();
        String contrasenia = new String(campoContrasenia.getPassword());

        try {
            if (usuario.isEmpty()) {
                throw new IllegalArgumentException("El nombre de usuario no puede estar vacio.");
            }
            validarLongitudContrasenia(contrasenia);

            Jugador nuevoJugador = new Jugador(usuario, contrasenia);
            repoJugadores.agregar(nuevoJugador);

            JOptionPane.showMessageDialog(this, "Jugador registrado. Iniciando sesion...");

            PantallaMenuPrincipal menu = new PantallaMenuPrincipal(nuevoJugador, repoJugadores, repoHistorial);
            menu.setVisible(true);
            this.dispose();
            pantallaInicio.dispose();

        } catch (PasswordInvalidaException | UsuarioDuplicadoException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Error de registro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void validarLongitudContrasenia(String contrasenia) throws PasswordInvalidaException {
        if (contrasenia.length() != LONGITUD_CONTRASENIA) {
            throw new PasswordInvalidaException();
        }
    }

    private void volverAInicio() {
        pantallaInicio.setVisible(true);
        this.dispose();
    }
}
