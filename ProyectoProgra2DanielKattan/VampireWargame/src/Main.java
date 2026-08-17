import datos.RepositorioHistorial;
import datos.RepositorioHistorialArreglo;
import datos.RepositorioJugadores;
import datos.RepositorioJugadoresArreglo;
import vista.PantallaInicio;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RepositorioJugadores repoJugadores = new RepositorioJugadoresArreglo();
            RepositorioHistorial repoHistorial = new RepositorioHistorialArreglo();
            PantallaInicio pantallaInicio = new PantallaInicio(repoJugadores, repoHistorial);
            pantallaInicio.setVisible(true);
        });
    }
}
