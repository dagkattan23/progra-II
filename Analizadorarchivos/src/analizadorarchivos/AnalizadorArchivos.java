package analizadorarchivos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class AnalizadorArchivos extends JFrame {

    private JTextField campoRuta;
    private JTextField campoBusqueda;  
    private JTextArea areaResultados;

    private int contadorTxt;
    private int contadorJava;
    private int contadorPdf;
    private int contadorOtros;

    public AnalizadorArchivos() {
        super("Analizador del Sistema de Archivos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelEntrada = new JPanel(new GridLayout(2, 2, 5, 5));
        panelEntrada.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelEntrada.add(new JLabel("Ruta en el directorio raiz:"));
        campoRuta = new JTextField();
        panelEntrada.add(campoRuta);

        panelEntrada.add(new JLabel("Texto de busqueda:"));
        campoBusqueda = new JTextField();
        panelEntrada.add(campoBusqueda);

        JButton btnContar = new JButton("Contar los archivos por extension");
        JButton btnBuscar = new JButton("Buscar los archivos por su nombre");

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnContar);
        panelBotones.add(btnBuscar);

        areaResultados = new JTextArea();
        areaResultados.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaResultados);

        add(panelEntrada, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        btnContar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                accionContar();
            }
        });

        btnBuscar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                accionBuscar();
            }
        });
    }

    private void accionContar() {
        String ruta = campoRuta.getText().trim();
        File directorio = new File(ruta);

        if (!validarDirectorio(directorio)) {
            return;
        }

        contadorTxt = 0;
        contadorJava = 0;
        contadorPdf = 0;
        contadorOtros = 0;

        contarArchivosPorExtension(directorio);

        String resultado = "Conteo de los archivos mediante extension"
                + "TXT: " + contadorTxt + " archivos\n"
                + "JAVA: " + contadorJava + " archivos\n"
                + "PDF: " + contadorPdf + " archivos\n"
                + "OTROS: " + contadorOtros + " archivos\n";

        areaResultados.setText(resultado);
    }

    private void contarArchivosPorExtension(File directorio) {
        File[] elementos = directorio.listFiles();

        if (elementos == null) {
            return;
        }

        for (File elemento : elementos) {
            if (elemento.isDirectory()) {
                contarArchivosPorExtension(elemento);
            } else {
                clasificarArchivo(elemento);
            }
        }
    }

    private void clasificarArchivo(File archivo) {
        String nombre = archivo.getName().toLowerCase();

        if (nombre.endsWith(".txt")) {
            contadorTxt++;
        } else if (nombre.endsWith(".java")) {
            contadorJava++;
        } else if (nombre.endsWith(".pdf")) {
            contadorPdf++;
        } else {
            contadorOtros++;
        }
    }

    private void accionBuscar() {
        String ruta = campoRuta.getText().trim();
        String texto = campoBusqueda.getText().trim();
        File directorio = new File(ruta);

        if (!validarDirectorio(directorio)) {
            return;
        }

        StringBuilder resultado = new StringBuilder();
        resultado.append("Busqueda de los archivos ").append(texto).append(" ===\n");

        int encontrados = buscarArchivosPorNombre(directorio, texto, resultado);

        if (encontrados == 0) {
            resultado.append("No se encontraron archivos que tengan las caracteristicas mencionadas.\n");
        }

        areaResultados.setText(resultado.toString());
    }

    private int buscarArchivosPorNombre(File directorio, String texto, StringBuilder resultado) {
        File[] elementos = directorio.listFiles();
        int encontrados = 0;

        if (elementos == null) {
            return encontrados;
        }

        for (File elemento : elementos) {
            if (elemento.isDirectory()) {
                encontrados += buscarArchivosPorNombre(elemento, texto, resultado);
            } else {
                if (elemento.getName().toLowerCase().contains(texto.toLowerCase())) {
                    resultado.append(elemento.getAbsolutePath()).append("\n");
                    encontrados++;
                }
            }
        }

        return encontrados;
    }

    private boolean validarDirectorio(File directorio) {
        if (!directorio.exists()) {
            JOptionPane.showMessageDialog(this,
                    "La ruta ingresada no existe.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!directorio.isDirectory()) {
            JOptionPane.showMessageDialog(this,
                    "La ruta ingresada no corresponde a un directorio valido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                AnalizadorArchivos ventana = new AnalizadorArchivos();
                ventana.setVisible(true);
            }
        });
    }
}