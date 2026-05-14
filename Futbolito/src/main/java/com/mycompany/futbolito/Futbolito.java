package com.mycompany.futbolito;

import com.mycompany.futbolito.vistas.VistaLogin;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Futbolito {

    public static void main(String[] args) {
        // 1. Configurar el aspecto visual del sistema (Look and Feel)
        /*try {
            // Esto hace que las ventanas se vean como las de Windows/Sistema Operativo actual
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            System.err.println("No se pudo configurar el Look and Feel.");
        }*/

        // 2. Ejecutar la interfaz en el hilo de eventos de Swing (Buena práctica)
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Instanciamos y mostramos el Login
                VistaLogin login = new VistaLogin();
                login.setLocationRelativeTo(null); // Centrar en la pantalla
                login.setVisible(true);
            }
        });
    }
}

/*package com.mycompany.futbolito;

import com.mycompany.futbolito.vistas.Menu;
import com.mycompany.futbolito.vistas.VistaParticipante;
import com.mycompany.futbolito.vistas.VistaParticipante;

public class Futbolito {

    public static void main(String[] args) {
        Menu menuPrincipal = new Menu();
        VistaParticipante v = new VistaParticipante();
        // Esta línea hace que la ventana de Windows se abra maximizada desde el inicio
        menuPrincipal.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        menuPrincipal.abrirFormularioHijo(v);
        menuPrincipal.setVisible(true);
    }
}*/
