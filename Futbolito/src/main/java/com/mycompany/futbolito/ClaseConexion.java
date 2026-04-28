package com.mycompany.futbolito;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class ClaseConexion {

    private Connection conectar = null;
    private String user = "postgres";
    private String password = "postgres";
    private String bd = "Futbolito";
    private String ip = "localhost";
    private String puerto = "5432";
    private String cadena = "jdbc:postgresql://" + ip + ":" + puerto + "/" + bd;

    public Connection establecerConexion() {
        try {
            Class.forName("org.postgresql.Driver");
            this.conectar = DriverManager.getConnection(cadena, user, password);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.toString());
        }
        return this.conectar;
    }
}
