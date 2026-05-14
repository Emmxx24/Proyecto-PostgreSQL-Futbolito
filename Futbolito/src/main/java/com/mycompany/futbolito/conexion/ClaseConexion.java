package com.mycompany.futbolito.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;
import com.mycompany.futbolito.utilidades.SesionGlobal;

public class ClaseConexion {

    private Connection conectar = null;
    String user = SesionGlobal.usuarioDB;
    String password = SesionGlobal.passwordDB;
    private String bd = "Futbolito";
    private String ip = "localhost";
    private String puerto = "5432";
    private String cadena = "jdbc:postgresql://" + ip + ":" + puerto + "/" + bd;

    public Connection establecerConexion() {
        try {
            Class.forName("org.postgresql.Driver");
            this.conectar = DriverManager.getConnection(cadena, user, password);
        } catch (Exception e) {
            //JOptionPane.showMessageDialog(null, "Error: " + e.toString());
            JOptionPane.showMessageDialog(null, "Datos incorrectos");
        }
        return this.conectar;
    }
}
