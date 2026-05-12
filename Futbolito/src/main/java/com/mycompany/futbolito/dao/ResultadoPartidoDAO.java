package com.mycompany.futbolito.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;
import com.mycompany.futbolito.conexion.ClaseConexion;

public class ResultadoPartidoDAO {

    public DefaultTableModel obtenerModeloResultados() throws Exception {
        DefaultTableModel modelo = new DefaultTableModel();
        
        modelo.addColumn("IdResultado");   // 0 (Oculto)
        modelo.addColumn("IdPartido");     // 1 (Oculto)
        modelo.addColumn("Partido");       // 2
        modelo.addColumn("Goles Local");   // 3
        modelo.addColumn("Goles Visitante");// 4
        modelo.addColumn("Hora de Término");// 5

        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();

        String sql = "SELECT rp.IdResultado, rp.IdPartido, " +
                     "CONCAT('EL: ', el.NombreEquipo, ' - EV: ', ev.NombreEquipo, ' - ', TO_CHAR(p.Fecha, 'DD/MM/YYYY'), ' [', l.Nombre, ']') AS Partido, " +
                     "rp.GolesLocal, rp.GolesVisitante, rp.HoraFin " +
                     "FROM Evento.ResultadoPartido rp " +
                     "INNER JOIN Evento.Partido p ON rp.IdPartido = p.IdPartido " +
                     "INNER JOIN Club.Equipo el ON p.IdLocal = el.IdEquipo " +
                     "INNER JOIN Club.Equipo ev ON p.IdVisitante = ev.IdEquipo " +
                     "INNER JOIN Juego.Lugar l ON p.IdLugar = l.IdLugar "; //+
                     //"ORDER BY rp.IdResultado DESC";
                     
        PreparedStatement ps = cn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getLong("IdResultado"),
                rs.getLong("IdPartido"),
                rs.getString("Partido"),
                rs.getInt("GolesLocal"),
                rs.getInt("GolesVisitante"),
                rs.getTime("HoraFin")
            });
        }
        return modelo;
    }

    public String obtenerDetallePartido(long idPartido) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "SELECT CONCAT('EL: ', el.NombreEquipo, ' - EV: ', ev.NombreEquipo, ' - ', TO_CHAR(p.Fecha, 'DD/MM/YYYY'), ' [', l.Nombre, ']') AS Partido " +
                     "FROM Evento.Partido p " +
                     "INNER JOIN Club.Equipo el ON p.IdLocal = el.IdEquipo " +
                     "INNER JOIN Club.Equipo ev ON p.IdVisitante = ev.IdEquipo " +
                     "INNER JOIN Juego.Lugar l ON p.IdLugar = l.IdLugar " +
                     "WHERE p.IdPartido = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idPartido);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("Partido");
        }
        return "";
    }

    public boolean verificaHoraFinInicio(long idPartido, java.sql.Time horaFin) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        // Si el COUNT > 0, significa que la Hora de Inicio es MAYOR o IGUAL a la hora de fin (Error Lógico)
        String sql = "SELECT COUNT(*) FROM Evento.Partido WHERE IdPartido = ? AND HoraInicio >= ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idPartido);
        ps.setTime(2, horaFin);
        ResultSet rs = ps.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }

    public void insertarResultado(long idPartido, java.sql.Time horaFin) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        // Insertamos los goles forzosamente en 0 como pidió la maestra
        String sql = "INSERT INTO Evento.ResultadoPartido (IdPartido, GolesLocal, GolesVisitante, HoraFin) VALUES (?, 0, 0, ?)";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idPartido);
        ps.setTime(2, horaFin);
        ps.executeUpdate();
    }

    public void modificarResultado(long idResultado, java.sql.Time horaFin) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        // Solo modificamos la hora, NO los goles, para respetar el Trigger
        String sql = "UPDATE Evento.ResultadoPartido SET HoraFin = ? WHERE IdResultado = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setTime(1, horaFin);
        ps.setLong(2, idResultado);
        ps.executeUpdate();
    }

    public void eliminarResultado(long idResultado) throws Exception {
        ClaseConexion obj = new ClaseConexion();
        Connection cn = obj.establecerConexion();
        String sql = "DELETE FROM Evento.ResultadoPartido WHERE IdResultado = ?";
        PreparedStatement ps = cn.prepareStatement(sql);
        ps.setLong(1, idResultado);
        ps.executeUpdate();
    }
}