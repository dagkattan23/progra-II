package descargamultiple;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class DescargaMultiple extends JFrame {
    
    private JProgressBar barra1, barra2, barra3;
    private JButton btnIniciar, btnCancelar;
    private JTextArea bitacora;
     
    private volatile boolean cancelado = false;
    private int descargasCompletadas = 0;

    public DescargaMultiple() {
        super("Descarga Multiple");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 450);
        setLocationRelativeTo(null);
        
        barra1 = new JProgressBar(0, 100);
        barra1.setStringPainted(true);
        barra2 = new JProgressBar(0, 100);
        barra2.setStringPainted(true);
        barra3 = new JProgressBar(0, 100);
        barra3.setStringPainted(true);
        
        btnIniciar = new JButton("Comenzar con las descargas");
        btnCancelar = new JButton("Cancelar las descargas");
        
        bitacora = new JTextArea();
        bitacora.setEditable(false);
        JScrollPane scrollBitacora = new JScrollPane(bitacora);
        
        JPanel panelBarras = new JPanel(new GridLayout(3, 1, 10, 10));
        panelBarras.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelBarras.add(crearFilaBarra("Archivo 1", barra1));
        panelBarras.add(crearFilaBarra("Archivo 2", barra2));
        panelBarras.add(crearFilaBarra("Archivo 3", barra3));
        
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnIniciar);
        panelBotones.add(btnCancelar);
        
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelBarras, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollBitacora, BorderLayout.CENTER);
        
        btnIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarDescargas();
            }
        });
        
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelado = true;
            }
        });
    }
    
    private JPanel crearFilaBarra(String nombre, JProgressBar barra) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(new JLabel(nombre), BorderLayout.WEST);
        panel.add(barra, BorderLayout.CENTER);
        return panel;
    }
    
    private void iniciarDescargas() {
        cancelado = false;
        descargasCompletadas = 0;
        bitacora.setText("");
        barra1.setValue(0);
        barra2.setValue(0);
        barra3.setValue(0);
        
        Thread hilo1 = new Thread(new TareaDescarga("Archivo 1", barra1));
        Thread hilo2 = new Thread(new TareaDescarga("Archivo 2", barra2));
        Thread hilo3 = new Thread(new TareaDescarga("Archivo 3", barra3));
        
        hilo1.start();
        hilo2.start();
        hilo3.start();
    }
    
    public void registrarEnBitacora(String mensaje) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                bitacora.append(mensaje + "\n");
            }
        });
    }
    
    public synchronized void sumarDescargaCompletada() {
        descargasCompletadas++;
        if (descargasCompletadas == 3) {
            registrarEnBitacora("Se completaron todas las descargas.");
        }
    }

    private class TareaDescarga implements Runnable {
        private String nombreArchivo;
        private JProgressBar barraProgreso;
        
        public TareaDescarga(String nombreArchivo, JProgressBar barraProgreso) {
            this.nombreArchivo = nombreArchivo;
            this.barraProgreso = barraProgreso;
        }
        
        @Override
        public void run() {
            int progreso = 0;
            Random random = new Random();
            
            while (progreso < 100 && !cancelado) {
                try {
                    Thread.sleep(random.nextInt(400) + 100); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                if (cancelado) {
                    break;
                }
                
                progreso += random.nextInt(15) + 5; 
                if (progreso > 100) {
                    progreso = 100;
                }
                
                final int progresoActual = progreso;
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        barraProgreso.setValue(progresoActual);
                    }
                });
                
                if (progresoActual < 100) {
                    registrarEnBitacora(nombreArchivo + ": descarga al " + progresoActual + "%");
                } else {
                    registrarEnBitacora(nombreArchivo + ": descarga completada");
                    sumarDescargaCompletada();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DescargaMultiple().setVisible(true);
            }
        });
    }
}
