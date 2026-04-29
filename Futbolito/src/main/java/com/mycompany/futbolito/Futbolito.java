package com.mycompany.futbolito;

import com.mycompany.futbolito.vistas.VistaParticipante;

public class Futbolito {

    public static void main(String[] args) {
        VistaParticipante vp = new VistaParticipante();
        vp.setLocationRelativeTo(null); // Para que salga centrada
        vp.setVisible(true);
    }
}
