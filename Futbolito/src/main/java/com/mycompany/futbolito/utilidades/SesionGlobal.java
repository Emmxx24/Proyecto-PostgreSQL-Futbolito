package com.mycompany.futbolito.utilidades;

public class SesionGlobal {

    public static String usuarioDB = "postgres"; // Por defecto
    public static String passwordDB = "tu_password_original";
    public static String rol = "Admin"; // Puede ser "Admin", "Arbitro", "Capturista"
}

/*Ejemplo de como se va a manejar lo de los roles para desactivar botones dentro de formularios:

    if (SesionGlobal.rol.equals("Arbitro")) {
        btnAgregar.setEnabled(false);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
        // Modo puro chisme (solo lectura)
    }

*/

/*Ejemplo de como se va a manejar el ocultar opciones del menu principal

    public MenuPrincipal() {
        initComponents();

        if (SesionGlobal.rol.equals("Arbitro")) {
            menuParticipantes.setVisible(false); // Ocultas el menú de arriba
            menuEquipos.setVisible(false); 
            // Solo dejas visible lo de partidos, resultados, etc.
        } else if (SesionGlobal.rol.equals("Capturista")) {
            menuPartidos.setVisible(false);
            menuResultados.setVisible(false);
            // Dejas visible lo de inscribir
        }
    }

*/