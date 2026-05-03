package com.mycompany.futbolito;

import com.mycompany.futbolito.vistas.VistaParticipante;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;


public class Futbolito {

    public static void main(String[] args) {
// 1. Creamos un JFrame "pirata" solo para hacer pruebas
        JFrame framePruebas = new JFrame("Entorno de Pruebas - Futbolito");
        framePruebas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        framePruebas.setSize(1000, 700); // Tamaño de la ventana principal
        framePruebas.setLocationRelativeTo(null); // Centrar en la pantalla

        // 2. Creamos el escritorio (El fondo gris donde viven los InternalFrames)
        JDesktopPane escritorioTemporal = new JDesktopPane();
        framePruebas.setContentPane(escritorioTemporal);

        // 3. Instanciamos tu VistaParticipante y la metemos al escritorio
        VistaParticipante vista = new VistaParticipante();
        escritorioTemporal.add(vista);
        
        // 4. Mostramos todo
        vista.setVisible(true);
        framePruebas.setVisible(true);
        
        // Opcional: Maximizar tu VistaParticipante para que llene el espacio
        try {
            vista.setMaximum(true);
        } catch (Exception e) {
            System.out.println("No se pudo maximizar.");
        }
    }
}
