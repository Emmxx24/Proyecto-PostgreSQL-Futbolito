package com.mycompany.futbolito.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;
import com.mycompany.futbolito.conexion.ClaseConexion;
import com.mycompany.futbolito.modelos.Jugador;

public class JugadorDAO {

    // Método extra para traer el nombre CON LA EDAD
    public String obtenerNombreParticipante(long idParticipante) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        java.sql.Connection cn = objetoConexion.establecerConexion();
        
        // Hacemos la misma concatenación que en tu tabla
        String sql = "SELECT CONCAT(NombreParticipante, ' (', Edad, ' años)') AS NombreConEdad FROM Persona.Participante WHERE IdParticipante = ?";
        java.sql.PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idParticipante);
        
        java.sql.ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("NombreConEdad");
        }
        return ""; 
    }

    public DefaultTableModel obtenerModeloJugadores() throws Exception {
        DefaultTableModel modelo = new DefaultTableModel();
        // Agregamos las columnas igual que en tu C#
        modelo.addColumn("ID Jugador"); // Esta la vamos a ocultar visualmente
        modelo.addColumn("ID Participante");
        modelo.addColumn("Nombre (Edad)");
        modelo.addColumn("Posición");
        modelo.addColumn("Número");
        modelo.addColumn("Tipo Sangre");
        modelo.addColumn("Amarillas");
        modelo.addColumn("Estado");

        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "SELECT j.IdJugador, p.IdParticipante,  " +
                     "CONCAT(p.NombreParticipante, ' (', p.Edad, ' años)') AS NombreConEdad, " +
                     "j.Posicion, j.Numero, j.TipoSangre, j.AcumuladorAmarillas, j.Estado " +
                     "FROM Persona.Jugador j " +
                     "INNER JOIN Persona.Participante p ON j.IdParticipante = p.IdParticipante "+
                     "ORDER BY j.IdJugador";
                     
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getLong("IdJugador"),
                rs.getLong("IdParticipante"),
                rs.getString("NombreConEdad"),
                rs.getString("Posicion"),
                rs.getInt("Numero"),
                rs.getString("TipoSangre"),
                rs.getInt("AcumuladorAmarillas"),
                rs.getString("Estado")
            });
        }
        return modelo;
    }

    public void insertarJugador(Jugador j) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        // Mandamos el 0 y 'Activo' por defecto desde la consulta, igual que en tu C#
        String sql = "INSERT INTO Persona.Jugador (IdParticipante, Posicion, Numero, TipoSangre, AcumuladorAmarillas, Estado) VALUES (?, ?, ?, ?, 0, 'Activo')";
        PreparedStatement ps = cn.prepareStatement(sql);
        
        ps.setLong(1, j.getIdParticipante());
        ps.setString(2, j.getPosicion());
        ps.setInt(3, j.getNumero());
        ps.setString(4, j.getTipoSangre());

        ps.executeUpdate();
    }

    public void modificarJugador(Jugador j) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "UPDATE Persona.Jugador SET Posicion = ?, Numero = ?, TipoSangre = ? WHERE IdJugador = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        
        ps.setString(1, j.getPosicion());
        ps.setInt(2, j.getNumero());
        ps.setString(3, j.getTipoSangre());
        ps.setLong(4, j.getIdJugador()); 

        ps.executeUpdate();
    }

    public void eliminarJugador(long idJugador) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "DELETE FROM Persona.Jugador WHERE IdJugador = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idJugador);

        ps.executeUpdate();
    }
}