package com.mycompany.futbolito.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;
import com.mycompany.futbolito.conexion.ClaseConexion;
import com.mycompany.futbolito.modelos.Participante;

public class ParticipanteDAO {

    // 1. OBTENER DATOS PARA LA TABLA
    public DefaultTableModel obtenerModeloParticipantes() throws Exception {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID Participante");
        modelo.addColumn("Nombre");
        modelo.addColumn("Género");
        modelo.addColumn("Teléfono");
        modelo.addColumn("Correo Electrónico");
        modelo.addColumn("Fecha Nacimiento");
        modelo.addColumn("Edad");

        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();
        
        if (cn == null) throw new Exception("No hay conexión a PostgreSQL.");

        String sql = "SELECT * FROM Persona.Participante p ORDER BY p.IdParticipante";
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getLong("IdParticipante"),
                rs.getString("NombreParticipante"),
                rs.getString("Genero"),
                rs.getString("Telefono"),
                rs.getString("CorreoElectronico"),
                rs.getDate("FechaNacimiento"),
                rs.getInt("Edad") // Calculado automáticamente por el Trigger
            });
        }
        return modelo;
    }

    // 2. INSERTAR
    public void insertarParticipante(Participante p) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "INSERT INTO Persona.Participante (NombreParticipante, Genero, Telefono, CorreoElectronico, FechaNacimiento) VALUES (?,?,?,?,?)";
        PreparedStatement ps = cn.prepareStatement(sql);
        
        ps.setString(1, p.getNombre());
        ps.setString(2, p.getGenero());
        ps.setString(3, p.getTelefono());
        ps.setString(4, p.getCorreo());
        ps.setDate(5, p.getFechaNacimiento());

        ps.executeUpdate();
    }

    // 3. MODIFICAR
    public void modificarParticipante(Participante p) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "UPDATE Persona.Participante SET NombreParticipante = ?, Genero = ?, Telefono = ?, CorreoElectronico = ?, FechaNacimiento = ? WHERE IdParticipante = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        
        ps.setString(1, p.getNombre());
        ps.setString(2, p.getGenero());
        ps.setString(3, p.getTelefono());
        ps.setString(4, p.getCorreo());
        ps.setDate(5, p.getFechaNacimiento());
        ps.setLong(6, p.getIdParticipante()); 

        ps.executeUpdate();
    }

    // 4. ELIMINAR
    public void eliminarParticipante(long idParticipante) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();

        String sql = "DELETE FROM Persona.Participante WHERE IdParticipante = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idParticipante);

        ps.executeUpdate();
    }

    // 5. VALIDACIÓN DE NEGOCIO (Equivalente a tu verificaRegistro de C#)
    // tipo 1 = Jugador, tipo 2 = Arbitro
    public boolean yaEstaRegistradoComo(long idParticipante, int tipo) throws Exception {
        ClaseConexion objetoConexion = new ClaseConexion();
        Connection cn = objetoConexion.establecerConexion();
        
        String tabla = (tipo == 1) ? "Persona.Jugador" : "Persona.Arbitro";
        String sql = "SELECT COUNT(*) FROM " + tabla + " WHERE IdParticipante = ?";
        
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idParticipante);
        
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0; // Si es mayor a 0, devuelve true (ya está registrado)
        }
        return false;
    }
}