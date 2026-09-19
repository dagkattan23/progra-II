package lab9_prograii;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class VentanaPrincipal extends JFrame {

    private static final int NUM_CLASIFICADORES = 3;
    private static final int NUM_EMPAQUETADORES = 2;
    private static final int[] CAPACIDADES_REPARTIDORES = {5, 4, 6, 5};
    private static final String[] RUTAS_REPARTIDORES = {"Ruta 1", "Ruta 2", "Ruta 3", "Ruta 4"};

    private final Font fuenteMonoespaciada = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    private CentroLogistico centro;
    private SimulacionControl control;
    private Repartidor[] repartidores;

    private Thread hiloRecepcion;
    private Thread[] hilosClasificadores;
    private Thread[] hilosEmpaquetadores;
    private Thread[] hilosRepartidores;

    private JButton btnIniciar, btnPausar, btnReanudar, btnDetener, btnReiniciar, btnEstadisticas;

    private JLabel lblRecepcion, lblAlmacen, lblClasificacion, lblEmpaquetado, lblExpedicion;
    private JProgressBar barraRecepcion, barraAlmacen, barraClasificacion, barraEmpaquetado, barraExpedicion;
    private JTextArea areaRecepcion, areaAlmacen, areaClasificacion, areaEmpaquetado, areaExpedicion;
    private JLabel lblTrabajadoresClasificacion, lblTrabajadoresEmpaquetado;

    private JLabel[] lblRepartidores;
    private JTextArea areaRegistro;

    private Timer temporizadorActualizacion;

    public VentanaPrincipal() {
        super("Sistema de Paqueteria");
        centro = new CentroLogistico();
        control = new SimulacionControl();
        repartidores = crearRepartidores();

        construirInterfaz();
        iniciarTemporizador();
        actualizarBotones();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 750));
        pack();
        setLocationRelativeTo(null);
    }

    private Repartidor[] crearRepartidores() {
        Repartidor[] lista = new Repartidor[CAPACIDADES_REPARTIDORES.length];
        for (int i = 0; i < lista.length; i++) {
            lista[i] = new Repartidor(i + 1, "Repartidor " + (i + 1),
                    CAPACIDADES_REPARTIDORES[i], RUTAS_REPARTIDORES[i]);
        }
        return lista;
    }


    private void construirInterfaz() {
        setLayout(new BorderLayout(8, 8));
        add(construirCabecera(), BorderLayout.NORTH);

        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(new EmptyBorder(5, 10, 5, 10));

        panelCentral.add(construirFilaEtapasIniciales());
        panelCentral.add(Box.createVerticalStrut(6));
        panelCentral.add(construirPanelEmpaquetado());
        panelCentral.add(Box.createVerticalStrut(6));
        panelCentral.add(construirPanelExpedicion());
        panelCentral.add(Box.createVerticalStrut(6));
        panelCentral.add(construirPanelRepartidores());

        JScrollPane scrollCentral = new JScrollPane(panelCentral);
        scrollCentral.setBorder(null);
        scrollCentral.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollCentral, BorderLayout.CENTER);

        add(construirPanelRegistro(), BorderLayout.SOUTH);
    }

    private JPanel construirCabecera() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 5, 10));

        JLabel titulo = new JLabel("Sistema de paqueteria", SwingConstants.CENTER);
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        panel.add(titulo, BorderLayout.NORTH);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        btnIniciar = new JButton("INICIAR");
        btnPausar = new JButton("PAUSAR");
        btnReanudar = new JButton("REANUDAR");
        btnDetener = new JButton("DETENER");
        btnReiniciar = new JButton("REINICIAR");
        btnEstadisticas = new JButton("ESTADISTICAS");

        btnIniciar.addActionListener(e -> iniciarSimulacion());
        btnPausar.addActionListener(e -> pausarSimulacion());
        btnReanudar.addActionListener(e -> reanudarSimulacion());
        btnDetener.addActionListener(e -> detenerSimulacion());
        btnReiniciar.addActionListener(e -> reiniciarSimulacion());
        btnEstadisticas.addActionListener(e -> mostrarEstadisticas());

        botones.add(btnIniciar);
        botones.add(btnPausar);
        botones.add(btnReanudar);
        botones.add(btnDetener);
        botones.add(btnReiniciar);
        botones.add(btnEstadisticas);
        panel.add(botones, BorderLayout.CENTER);

        panel.add(construirLeyendaPrioridades(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel construirLeyendaPrioridades() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.add(crearEtiquetaPrioridad("URGENTE", Color.RED));
        panel.add(crearEtiquetaPrioridad("ALTA", new Color(255, 140, 0)));
        panel.add(crearEtiquetaPrioridad("NORMAL", new Color(200, 170, 0)));
        panel.add(crearEtiquetaPrioridad("BAJA", new Color(0, 150, 0)));
        return panel;
    }

    private JLabel crearEtiquetaPrioridad(String texto, Color color) {
        JLabel etiqueta = new JLabel("● " + texto);
        etiqueta.setForeground(color);
        etiqueta.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        return etiqueta;
    }

    private JPanel construirFilaEtapasIniciales() {
        JPanel fila = new JPanel(new GridLayout(1, 3, 8, 0));
        fila.add(construirPanelEtapa("RECEPCION", 'R'));
        fila.add(construirPanelEtapa("ALMACEN", 'A'));
        fila.add(construirPanelClasificacion());
        return fila;
    }

    private JPanel construirPanelClasificacion() {
        JPanel contenedor = construirPanelEtapa("CLASIFICACION", 'C');
        lblTrabajadoresClasificacion = new JLabel(" ");
        lblTrabajadoresClasificacion.setFont(fuenteMonoespaciada);
        lblTrabajadoresClasificacion.setBorder(new EmptyBorder(2, 6, 4, 6));
        contenedor.add(lblTrabajadoresClasificacion, BorderLayout.SOUTH);
        return contenedor;
    }

    
    private JPanel construirPanelEtapa(String titulo, char clave) {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), titulo));

        JLabel etiqueta = new JLabel(titulo + ": 0 / 0");
        JProgressBar barra = new JProgressBar();
        barra.setStringPainted(true);

        JTextArea area = new JTextArea(4, 15);
        area.setEditable(false);
        area.setFont(fuenteMonoespaciada);
        area.setLineWrap(true);
        area.setWrapStyleWord(false);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(etiqueta, BorderLayout.NORTH);
        norte.add(barra, BorderLayout.SOUTH);

        panel.add(norte, BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);

        switch (clave) {
            case 'R':
                lblRecepcion = etiqueta;
                barraRecepcion = barra;
                areaRecepcion = area;
                break;
            case 'A':
                lblAlmacen = etiqueta;
                barraAlmacen = barra;
                areaAlmacen = area;
                break;
            case 'C':
                lblClasificacion = etiqueta;
                barraClasificacion = barra;
                areaClasificacion = area;
                break;
            case 'E':
                lblEmpaquetado = etiqueta;
                barraEmpaquetado = barra;
                areaEmpaquetado = area;
                break;
            case 'X':
                lblExpedicion = etiqueta;
                barraExpedicion = barra;
                areaExpedicion = area;
                break;
            default:
                break;
        }
        return panel;
    }

    private JPanel construirPanelEmpaquetado() {
        JPanel contenedor = construirPanelEtapa("EMPAQUETADO", 'E');
        lblTrabajadoresEmpaquetado = new JLabel(" ");
        lblTrabajadoresEmpaquetado.setFont(fuenteMonoespaciada);
        lblTrabajadoresEmpaquetado.setBorder(new EmptyBorder(2, 6, 4, 6));
        contenedor.add(lblTrabajadoresEmpaquetado, BorderLayout.SOUTH);
        return contenedor;
    }

    private JPanel construirPanelExpedicion() {
        return construirPanelEtapa("EXPEDICION", 'X');
    }

    private JPanel construirPanelRepartidores() {
        JPanel panel = new JPanel(new GridLayout(1, repartidores.length, 8, 0));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "REPARTIDORES"));

        lblRepartidores = new JLabel[repartidores.length];
        for (int i = 0; i < repartidores.length; i++) {
            JLabel etiqueta = new JLabel();
            etiqueta.setVerticalAlignment(SwingConstants.TOP);
            etiqueta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    new EmptyBorder(6, 8, 6, 8)));
            lblRepartidores[i] = etiqueta;
            panel.add(etiqueta);
        }
        return panel;
    }

    private JPanel construirPanelRegistro() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(0, 10, 10, 10));
        panel.setPreferredSize(new Dimension(100, 180));

        areaRegistro = new JTextArea();
        areaRegistro.setEditable(false);
        areaRegistro.setFont(fuenteMonoespaciada);
        areaRegistro.setBackground(Color.BLACK);
        areaRegistro.setForeground(new Color(0, 230, 0));

        JScrollPane scroll = new JScrollPane(areaRegistro);
        scroll.setBorder(BorderFactory.createTitledBorder("REGISTRO DEL SISTEMA"));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void iniciarSimulacion() {
        if (control.isActivo()) {
            return;
        }
        control.iniciar();
        centro.registrarLog("Simulacion iniciada");

        hiloRecepcion = new RecepcionThread(centro, control);
        hiloRecepcion.start();

        hilosClasificadores = new Thread[NUM_CLASIFICADORES];
        for (int i = 0; i < NUM_CLASIFICADORES; i++) {
            hilosClasificadores[i] = new ClasificadorThread(i + 1, centro, control);
            hilosClasificadores[i].start();
        }

        hilosEmpaquetadores = new Thread[NUM_EMPAQUETADORES];
        for (int i = 0; i < NUM_EMPAQUETADORES; i++) {
            hilosEmpaquetadores[i] = new EmpaquetadorThread(i + 1, centro, control);
            hilosEmpaquetadores[i].start();
        }

        hilosRepartidores = new Thread[repartidores.length];
        for (int i = 0; i < repartidores.length; i++) {
            hilosRepartidores[i] = new RepartidorThread(repartidores[i], centro, control);
            hilosRepartidores[i].start();
        }

        actualizarBotones();
    }

    private void pausarSimulacion() {
        control.pausar();
        centro.registrarLog("Simulacion pausada");
        actualizarBotones();
    }

    private void reanudarSimulacion() {
        control.reanudar();
        centro.registrarLog("Simulacion reanudada");
        actualizarBotones();
    }

    private void detenerSimulacion() {
        control.detener();
        interrumpirTodos();
        centro.registrarLog("Simulacion detenida");
        actualizarBotones();
    }

    private void reiniciarSimulacion() {
        control.detener();
        interrumpirTodos();

        centro.reiniciar();
        repartidores = crearRepartidores();
        for (int i = 0; i < lblRepartidores.length; i++) {
            lblRepartidores[i].setText("");
        }

        centro.registrarLog("Sistema reiniciado");
        actualizarPaneles();
        actualizarBotones();
    }

    private void interrumpirTodos() {
        if (hiloRecepcion != null) {
            hiloRecepcion.interrupt();
        }
        interrumpirGrupo(hilosClasificadores);
        interrumpirGrupo(hilosEmpaquetadores);
        interrumpirGrupo(hilosRepartidores);
    }

    private void interrumpirGrupo(Thread[] hilos) {
        if (hilos == null) {
            return;
        }
        for (Thread hilo : hilos) {
            if (hilo != null) {
                hilo.interrupt();
            }
        }
    }

    private void actualizarBotones() {
        boolean activo = control.isActivo();
        boolean pausado = control.isPausado();
        btnIniciar.setEnabled(!activo);
        btnPausar.setEnabled(activo && !pausado);
        btnReanudar.setEnabled(activo && pausado);
        btnDetener.setEnabled(activo);
        btnReiniciar.setEnabled(true);
    }

    private void mostrarEstadisticas() {
        int generados = centro.getTotalGenerados();
        int entregados = centro.getTotalEntregados();
        int devueltos = centro.getTotalDevueltos();
        int pendientes = centro.getListaRecepcion().tamaño();
        int enProceso = generados - entregados - devueltos;
        if (enProceso < 0) {
            enProceso = 0;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Estadisticas");
        sb.append(String.format("Paquetes generados: %d%n", generados));
        sb.append(String.format("Entregados:         %d%n", entregados));
        sb.append(String.format("Devueltos:          %d%n", devueltos));
        sb.append(String.format("En proceso:         %d%n", enProceso));
        sb.append(String.format("Pendientes:         %d%n%n", pendientes));
        sb.append(String.format("Tiempo promedio:    %.1f s%n%n", centro.getTiempoPromedio()));

        for (Repartidor r : repartidores) {
            sb.append(String.format("%-14s %d%n", r.getNombre() + ":", r.getPaquetesEntregados()));
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(fuenteMonoespaciada);
        JOptionPane.showMessageDialog(this, area, "Estadisticas del sistema", JOptionPane.PLAIN_MESSAGE);
    }

    private void iniciarTemporizador() {
        temporizadorActualizacion = new Timer(400, e -> actualizarPaneles());
        temporizadorActualizacion.start();
    }

    private void actualizarPaneles() {
        actualizarEtapa(centro.getListaRecepcion(), lblRecepcion, barraRecepcion, areaRecepcion, "RECEPCION");
        actualizarEtapa(centro.getListaAlmacen(), lblAlmacen, barraAlmacen, areaAlmacen, "ALMACEN");
        actualizarEtapa(centro.getListaClasificacion(), lblClasificacion, barraClasificacion, areaClasificacion, "CLASIFICACION");
        actualizarEtapa(centro.getListaEmpaquetado(), lblEmpaquetado, barraEmpaquetado, areaEmpaquetado, "EMPAQUETADO");
        actualizarEtapa(centro.getListaExpedicion(), lblExpedicion, barraExpedicion, areaExpedicion, "EXPEDICION");

        actualizarTrabajadores();
        actualizarRepartidores();
        actualizarRegistro();
    }

    private void actualizarEtapa(ListaEnlazada<Paquete> lista, JLabel etiqueta, JProgressBar barra,
                                  JTextArea area, String nombre) {
        int tamaño = lista.tamaño();
        int capacidad = lista.getCapacidadMaxima();

        etiqueta.setText(nombre + ": " + tamaño + " / " + capacidad);
        barra.setMaximum(capacidad);
        barra.setValue(tamaño);
        barra.setString(tamaño + "/" + capacidad);

        StringBuilder sb = new StringBuilder();
        lista.recorrer(p -> sb.append(formatoPaquete(p)).append("  "));
        area.setText(sb.toString());
    }

    private String formatoPaquete(Paquete p) {
        return p.getCodigo() + "[" + p.getPrioridad().toString().charAt(0) + "]";
    }

    private void actualizarTrabajadores() {
        StringBuilder sbClas = new StringBuilder("Clasificadores  ");
        for (int i = 1; i <= NUM_CLASIFICADORES; i++) {
            Paquete p = centro.getProcesando("Clasificador-" + i);
            sbClas.append(i).append(":").append(p != null ? p.getCodigo() : "--").append("   ");
        }
        
        if (lblTrabajadoresClasificacion != null) {
            lblTrabajadoresClasificacion.setText(sbClas.toString());
        }

        StringBuilder sbEmp = new StringBuilder("Empaquetadores  ");
        for (int i = 1; i <= NUM_EMPAQUETADORES; i++) {
            Paquete p = centro.getProcesando("Empaquetador-" + i);
            sbEmp.append(i).append(":").append(p != null ? p.getCodigo() : "--").append("   ");
        }
        lblTrabajadoresEmpaquetado.setText(sbEmp.toString());
    }

    private void actualizarRepartidores() {
        for (int i = 0; i < repartidores.length; i++) {
            Repartidor r = repartidores[i];
            String lleno = r.estaLleno() ? " [LLENO]" : "";
            String texto = "<html><b>" + r.getNombre() + "</b> (" + r.getRuta() + ")<br>"
                    + "Estado: " + r.getEstado() + "<br>"
                    + "Capacidad: " + r.getCargaActual() + "/" + r.getCapacidad() + lleno + "<br>"
                    + "Entregados: " + r.getPaquetesEntregados() + "</html>";
            lblRepartidores[i].setText(texto);
        }
    }

    private void actualizarRegistro() {
        StringBuilder sb = new StringBuilder();
        centro.getRegistro().recorrer(linea -> sb.append(linea).append("\n"));
        areaRegistro.setText(sb.toString());
        areaRegistro.setCaretPosition(areaRegistro.getDocument().getLength());
    }
}