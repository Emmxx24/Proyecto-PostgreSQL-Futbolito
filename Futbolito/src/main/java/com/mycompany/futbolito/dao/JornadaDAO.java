package com.mycompany.futbolito.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;
import com.mycompany.futbolito.conexion.ClaseConexion;

public class JornadaDAO {

    public DefaultTableModel obtenerModeloJornadas() throws Exception {
        // Hacemos que la tabla sea de solo lectura desde el modelo
        DefaultTableModel modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        modelo.addColumn("IdJornada"); // Columna oculta
        modelo.addColumn("Torneo");
        modelo.addColumn("Número de Jornada");

        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        // Consulta adaptada a PostgreSQL (TO_CHAR en lugar de CONVERT) pero con tu misma lógica
        String sql = "SELECT J.IdJornada, " +
                     "CONCAT(T.NombreTorneo, ' (', TO_CHAR(T.FechaInicio, 'DD/MM/YYYY'), ' - ', TO_CHAR(T.FechaFin, 'DD/MM/YYYY'), ')') AS Torneo, " +
                     "J.NumeroJornada " +
                     "FROM Juego.Jornada J " +
                     "INNER JOIN Juego.Torneo T ON J.IdTorneo = T.IdTorneo " +
                     "ORDER BY T.NombreTorneo, J.NumeroJornada";
                     
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getLong("IdJornada"),
                rs.getString("Torneo"),
                rs.getInt("NumeroJornada")
            });
        }
        return modelo;
    }
}