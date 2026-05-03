package com.mycompany.futbolito;

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
}
