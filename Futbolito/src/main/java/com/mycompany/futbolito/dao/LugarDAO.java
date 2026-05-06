package com.mycompany.futbolito.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;
import com.mycompany.futbolito.conexion.ClaseConexion;
import com.mycompany.futbolito.modelos.Lugar;

public class LugarDAO {

    public DefaultTableModel obtenerModeloLugares() throws Exception {
        DefaultTableModel modelo = new DefaultTableModel();
        
        modelo.addColumn("ID Lugar");
        modelo.addColumn("Nombre del Lugar");
        modelo.addColumn("Ubicación");
        modelo.addColumn("Capacidad");

        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "SELECT * FROM Juego.Lugar ORDER BY IdLugar ASC";
                     
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getLong("IdLugar"),
                rs.getString("Nombre"),
                rs.getString("Ubicacion"),
                rs.getInt("Capacidad")
            });
        }
        return modelo;
    }

    public void insertarLugar(Lugar l) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "INSERT INTO Juego.Lugar (Ubicacion, Nombre, Capacidad) VALUES (?, ?, ?)";
        PreparedStatement ps = cn.prepareStatement(sql);
        
        ps.setString(1, l.getUbicacion());
        ps.setString(2, l.getNombre());
        ps.setInt(3, l.getCapacidad());

        ps.executeUpdate();
    }

    public void modificarLugar(Lugar l) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "UPDATE Juego.Lugar SET Nombre = ?, Ubicacion = ?, Capacidad = ? WHERE IdLugar = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        
        ps.setString(1, l.getNombre());
        ps.setString(2, l.getUbicacion());
        ps.setInt(3, l.getCapacidad());
        ps.setLong(4, l.getIdLugar()); 

        ps.executeUpdate();
    }

    public void eliminarLugar(long idLugar) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "DELETE FROM Juego.Lugar WHERE IdLugar = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idLugar);

        ps.executeUpdate();
    }
}