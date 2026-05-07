package com.mycompany.futbolito.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;
import com.mycompany.futbolito.conexion.ClaseConexion;
import com.mycompany.futbolito.modelos.Arbitro;

public class ArbitroDAO {

    // Método para traer el nombre CON LA EDAD (Igual que en Jugador)
    public String obtenerNombreParticipante(long idParticipante) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();
        
        String sql = "SELECT CONCAT(NombreParticipante, ' (', Edad, ' años)') AS NombreConEdad FROM Persona.Participante WHERE IdParticipante = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idParticipante);
        
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("NombreConEdad");
        }
        return ""; 
    }

    // Método para llenar la tabla
    public DefaultTableModel obtenerModeloArbitros() throws Exception {
        DefaultTableModel modelo = new DefaultTableModel();
        
        // Agregamos el IdParticipante oculto en la posición 0
        modelo.addColumn("IdParticipante"); 
        modelo.addColumn("ID Árbitro");
        modelo.addColumn("Participante");
        modelo.addColumn("Cédula Profesional");

        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        // Concatenamos la edad directo desde Postgres igual que en C#
        String sql = "SELECT a.IdParticipante, a.IdArbitro, " +
                     "CONCAT(p.NombreParticipante, ' (', p.Edad, ' años)') AS NombreParticipante, " +
                     "a.CedulaArbitro " +
                     "FROM Persona.Arbitro a " +
                     "INNER JOIN Persona.Participante p ON a.IdParticipante = p.IdParticipante";
                     
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getLong("IdParticipante"),
                rs.getLong("IdArbitro"),
                rs.getString("NombreParticipante"),
                rs.getString("CedulaArbitro")
            });
        }
        return modelo;
    }

    public void insertarArbitro(Arbitro a) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "INSERT INTO Persona.Arbitro (IdParticipante, CedulaArbitro) VALUES (?, ?)";
        PreparedStatement ps = cn.prepareStatement(sql);
        
        ps.setLong(1, a.getIdParticipante());
        ps.setString(2, a.getCedulaArbitro());

        ps.executeUpdate();
    }

    public void modificarArbitro(Arbitro a) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "UPDATE Persona.Arbitro SET CedulaArbitro = ? WHERE IdArbitro = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        
        ps.setString(1, a.getCedulaArbitro());
        ps.setLong(2, a.getIdArbitro()); 

        ps.executeUpdate();
    }

    public void eliminarArbitro(long idArbitro) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "DELETE FROM Persona.Arbitro WHERE IdArbitro = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idArbitro);

        ps.executeUpdate();
    }
}