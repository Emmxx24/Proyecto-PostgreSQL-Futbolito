package com.mycompany.futbolito.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;
import com.mycompany.futbolito.conexion.ClaseConexion;
import com.mycompany.futbolito.modelos.Torneo;

public class TorneoDAO {

    public DefaultTableModel obtenerModeloTorneos() throws Exception {
        DefaultTableModel modelo = new DefaultTableModel();
        
        modelo.addColumn("ID Torneo");
        modelo.addColumn("Nombre del Torneo");
        modelo.addColumn("Edad Mínima");
        modelo.addColumn("Edad Máxima");
        modelo.addColumn("Género");
        modelo.addColumn("Fecha Inicio");
        modelo.addColumn("Fecha Fin");
        modelo.addColumn("Equipos");
        modelo.addColumn("Jornadas");

        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "SELECT * FROM Juego.Torneo ORDER BY IdTorneo ASC";
                     
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getLong("IdTorneo"),
                rs.getString("NombreTorneo"),
                rs.getInt("EdadMin"),
                rs.getInt("EdadMax"),
                rs.getString("Genero"),
                rs.getDate("FechaInicio"),
                rs.getDate("FechaFin"),
                rs.getInt("CantEquipos"),
                rs.getInt("NumJornadas")
            });
        }
        return modelo;
    }

    public void insertarTorneo(Torneo t) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        // Mandamos CantEquipos y NumJornadas como 0 por defecto, igual que en tu C#
        String sql = "INSERT INTO Juego.Torneo (NombreTorneo, EdadMin, EdadMax, Genero, FechaInicio, FechaFin, CantEquipos, NumJornadas) VALUES (?, ?, ?, ?, ?, ?, 0, 0)";
        PreparedStatement ps = cn.prepareStatement(sql);
        
        ps.setString(1, t.getNombreTorneo());
        ps.setInt(2, t.getEdadMin());
        ps.setInt(3, t.getEdadMax());
        ps.setString(4, t.getGenero());
        ps.setDate(5, t.getFechaInicio());
        ps.setDate(6, t.getFechaFin());

        ps.executeUpdate();
    }

    public void modificarTorneo(Torneo t) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "UPDATE Juego.Torneo SET NombreTorneo = ?, EdadMin = ?, EdadMax = ?, Genero = ?, FechaInicio = ?, FechaFin = ? WHERE IdTorneo = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        
        ps.setString(1, t.getNombreTorneo());
        ps.setInt(2, t.getEdadMin());
        ps.setInt(3, t.getEdadMax());
        ps.setString(4, t.getGenero());
        ps.setDate(5, t.getFechaInicio());
        ps.setDate(6, t.getFechaFin());
        ps.setLong(7, t.getIdTorneo()); 

        ps.executeUpdate();
    }

    public void eliminarTorneo(long idTorneo) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "DELETE FROM Juego.Torneo WHERE IdTorneo = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idTorneo);

        ps.executeUpdate();
    }
}
